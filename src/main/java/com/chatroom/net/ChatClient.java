package com.chatroom.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client-side socket wrapper: opens the connection, sends the initial
 * {@link MessageType#JOIN_REQUEST}, and runs a background thread that
 * forwards every incoming {@link ChatMessage} to a {@link ClientListener}
 * so the Swing GUI can react to it on the Event Dispatch Thread.
 */
public class ChatClient {

    public interface ClientListener {
        void onMessage(ChatMessage message);
        void onDisconnected(String reason);
    }

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenThread;
    private volatile boolean connected = false;
    private ClientListener listener;

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public void connect(String host, int port, String username, String room) throws IOException {
        socket = new Socket();
        socket.connect(new java.net.InetSocketAddress(host, port), 5000);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;

        listenThread = new Thread(this::listenLoop, "chat-client-listen");
        listenThread.setDaemon(true);
        listenThread.start();

        send(ChatMessage.joinRequest(username, room));
    }

    private void listenLoop() {
        try {
            while (connected) {
                ChatMessage message = (ChatMessage) in.readObject();
                if (listener != null) {
                    listener.onMessage(message);
                }
            }
        } catch (EOFException | java.net.SocketException e) {
            if (connected && listener != null) {
                listener.onDisconnected(e.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected && listener != null) {
                listener.onDisconnected(e.getMessage());
            }
        } finally {
            connected = false;
        }
    }

    public synchronized void send(ChatMessage message) {
        try {
            if (out != null && connected) {
                out.writeObject(message);
                out.flush();
                out.reset();
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onDisconnected(e.getMessage());
            }
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
