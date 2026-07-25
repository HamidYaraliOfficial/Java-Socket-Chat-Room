package com.chatroom.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Multi-threaded chat server: accepts one socket connection per client on
 * its own thread, groups clients into named rooms, and routes chat,
 * private-message, room-change and administrative events between them.
 * All UI feedback (log lines, connected-client table updates) is pushed
 * out through a {@link ServerListener} so the GUI never touches sockets
 * directly.
 */
public class ChatServer {

    public interface ServerListener {
        void onLog(String line);
        void onClientsChanged(List<ConnectedClientInfo> clients);
        void onServerStopped();
    }

    public static final String DEFAULT_ROOM = "General";

    private final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();
    private final Set<ClientHandler> allClients = new CopyOnWriteArraySet<>();

    private ServerSocket serverSocket;
    private ExecutorService pool;
    private volatile boolean running = false;
    private ServerListener listener;
    private int port;

    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public void start(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        running = true;
        pool = Executors.newCachedThreadPool();
        rooms.putIfAbsent(DEFAULT_ROOM, new CopyOnWriteArraySet<>());

        Thread acceptThread = new Thread(this::acceptLoop, "chat-server-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        log("Server started on port " + port);
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                pool.execute(handler);
            } catch (IOException e) {
                if (running) {
                    log("Accept error: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        for (ClientHandler client : allClients) {
            client.disconnect();
        }
        allClients.clear();
        rooms.clear();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        if (pool != null) {
            pool.shutdownNow();
        }
        log("Server stopped.");
        if (listener != null) {
            listener.onServerStopped();
        }
    }

    public void handleIncoming(ClientHandler handler, ChatMessage message) {
        switch (message.getType()) {
            case JOIN_REQUEST:
                handleJoin(handler, message);
                break;
            case CHAT:
                handleChat(handler, message);
                break;
            case PRIVATE_CHAT:
                handlePrivate(handler, message);
                break;
            case CHANGE_ROOM:
                handleRoomChange(handler, message);
                break;
            case PING:
                break;
            default:
                log("Unhandled message type from " + handler.getUsername() + ": " + message.getType());
        }
    }

    private synchronized void handleJoin(ClientHandler handler, ChatMessage message) {
        String requestedRoom = (message.getRoom() == null || message.getRoom().isBlank()) ? DEFAULT_ROOM : message.getRoom().trim();
        String username = message.getSender() == null ? "" : message.getSender().trim();

        Set<ClientHandler> roomClients = rooms.computeIfAbsent(requestedRoom, r -> new CopyOnWriteArraySet<>());
        boolean taken = roomClients.stream().anyMatch(c -> username.equalsIgnoreCase(c.getUsername()));
        if (username.isEmpty() || taken) {
            ChatMessage reject = new ChatMessage(MessageType.JOIN_REJECTED);
            handler.send(reject);
            return;
        }

        handler.setUsername(username);
        handler.setRoom(requestedRoom);
        roomClients.add(handler);
        allClients.add(handler);

        ChatMessage accepted = new ChatMessage(MessageType.JOIN_ACCEPTED);
        accepted.setRoom(requestedRoom);
        accepted.setSender(username);
        handler.send(accepted);

        broadcastUserJoined(requestedRoom, username, handler);
        broadcastUserList(requestedRoom);
        notifyClientsChanged();
        log(username + " joined room \"" + requestedRoom + "\" from " + handler.getAddress());
    }

    private void handleChat(ClientHandler handler, ChatMessage message) {
        String room = handler.getRoom();
        if (room == null) {
            return;
        }
        ChatMessage out = ChatMessage.chat(handler.getUsername(), room, message.getContent());
        for (ClientHandler c : roomSet(room)) {
            c.send(out);
        }
    }

    private void handlePrivate(ClientHandler handler, ChatMessage message) {
        String recipientName = message.getRecipient();
        if (recipientName == null) {
            return;
        }
        ChatMessage out = ChatMessage.privateChat(handler.getUsername(), recipientName, message.getContent());
        for (ClientHandler c : allClients) {
            if (recipientName.equalsIgnoreCase(c.getUsername())) {
                c.send(out);
                handler.send(out);
                return;
            }
        }
    }

    private synchronized void handleRoomChange(ClientHandler handler, ChatMessage message) {
        String oldRoom = handler.getRoom();
        String newRoom = message.getRoom() == null || message.getRoom().isBlank() ? DEFAULT_ROOM : message.getRoom().trim();
        if (newRoom.equals(oldRoom)) {
            return;
        }

        if (oldRoom != null) {
            Set<ClientHandler> oldSet = rooms.get(oldRoom);
            if (oldSet != null) {
                oldSet.remove(handler);
                broadcastUserLeft(oldRoom, handler.getUsername());
                broadcastUserList(oldRoom);
            }
        }

        Set<ClientHandler> newSet = rooms.computeIfAbsent(newRoom, r -> new CopyOnWriteArraySet<>());
        boolean taken = newSet.stream().anyMatch(c -> handler.getUsername().equalsIgnoreCase(c.getUsername()));
        if (taken) {
            handler.send(new ChatMessage(MessageType.JOIN_REJECTED));
            return;
        }
        newSet.add(handler);
        handler.setRoom(newRoom);

        ChatMessage accepted = new ChatMessage(MessageType.JOIN_ACCEPTED);
        accepted.setRoom(newRoom);
        handler.send(accepted);

        broadcastUserJoined(newRoom, handler.getUsername(), handler);
        broadcastUserList(newRoom);
        notifyClientsChanged();
        log(handler.getUsername() + " moved to room \"" + newRoom + "\"");
    }

    public void handleDisconnect(ClientHandler handler) {
        allClients.remove(handler);
        String room = handler.getRoom();
        if (room != null) {
            Set<ClientHandler> set = rooms.get(room);
            if (set != null) {
                set.remove(handler);
                broadcastUserLeft(room, handler.getUsername());
                broadcastUserList(room);
            }
        }
        notifyClientsChanged();
        if (handler.getUsername() != null) {
            log(handler.getUsername() + " disconnected.");
        }
    }

    public void kick(String username) {
        for (ClientHandler c : allClients) {
            if (username.equalsIgnoreCase(c.getUsername())) {
                c.send(new ChatMessage(MessageType.KICKED));
                c.disconnect();
                handleDisconnect(c);
                log("Kicked " + username);
                return;
            }
        }
    }

    public void broadcastServerNotice(String content) {
        ChatMessage notice = ChatMessage.serverNotice(content);
        for (ClientHandler c : allClients) {
            c.send(notice);
        }
        log("[Broadcast] " + content);
    }

    private void broadcastUserJoined(String room, String username, ClientHandler exclude) {
        ChatMessage joined = new ChatMessage(MessageType.USER_JOINED);
        joined.setRoom(room);
        joined.setSender(username);
        for (ClientHandler c : roomSet(room)) {
            if (c != exclude) {
                c.send(joined);
            }
        }
    }

    private void broadcastUserLeft(String room, String username) {
        ChatMessage left = new ChatMessage(MessageType.USER_LEFT);
        left.setRoom(room);
        left.setSender(username);
        for (ClientHandler c : roomSet(room)) {
            c.send(left);
        }
    }

    private void broadcastUserList(String room) {
        List<String> names = roomSet(room).stream()
                .map(ClientHandler::getUsername)
                .filter(n -> n != null)
                .sorted()
                .collect(Collectors.toList());
        ChatMessage listMsg = new ChatMessage(MessageType.USER_LIST);
        listMsg.setRoom(room);
        listMsg.setUserList(names);
        for (ClientHandler c : roomSet(room)) {
            c.send(listMsg);
        }
    }

    private Set<ClientHandler> roomSet(String room) {
        return rooms.getOrDefault(room, new CopyOnWriteArraySet<>());
    }

    private void notifyClientsChanged() {
        if (listener != null) {
            List<ConnectedClientInfo> snapshot = allClients.stream()
                    .map(ClientHandler::snapshot)
                    .collect(Collectors.toList());
            listener.onClientsChanged(snapshot);
        }
    }

    public void log(String line) {
        if (listener != null) {
            listener.onLog(line);
        }
    }
}
