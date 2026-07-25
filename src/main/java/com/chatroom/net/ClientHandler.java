package com.chatroom.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * Runs on its own thread for the lifetime of a single client connection:
 * reads {@link ChatMessage} objects off the socket and hands them to the
 * owning {@link ChatServer} for processing, and exposes a synchronized
 * {@link #send(ChatMessage)} so the server can push messages back at any
 * time from other threads.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private volatile String username;
    private volatile String room;
    private final LocalDateTime connectedAt = LocalDateTime.now();
    private volatile boolean running = true;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            while (running) {
                ChatMessage message = (ChatMessage) in.readObject();
                server.handleIncoming(this, message);
            }
        } catch (EOFException | java.net.SocketException closed) {
            // Normal disconnect path.
        } catch (IOException | ClassNotFoundException e) {
            server.log("Connection error for " + safeUsername() + ": " + e.getMessage());
        } finally {
            server.handleDisconnect(this);
            closeQuietly();
        }
    }

    public synchronized void send(ChatMessage message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
                out.reset();
            }
        } catch (IOException e) {
            server.log("Failed to send to " + safeUsername() + ": " + e.getMessage());
        }
    }

    public void disconnect() {
        running = false;
        closeQuietly();
    }

    private void closeQuietly() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private String safeUsername() {
        return username == null ? "?" : username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getAddress() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public ConnectedClientInfo snapshot() {
        return new ConnectedClientInfo(safeUsername(), getAddress(), room, connectedAt);
    }
}
