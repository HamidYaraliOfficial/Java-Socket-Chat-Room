package com.chatroom.net;

import java.time.LocalDateTime;

/**
 * Read-only snapshot of a connected client, used purely to populate the
 * server GUI's client table without exposing the live socket/thread
 * objects to the UI layer.
 */
public class ConnectedClientInfo {

    private final String username;
    private final String address;
    private final String room;
    private final LocalDateTime connectedAt;

    public ConnectedClientInfo(String username, String address, String room, LocalDateTime connectedAt) {
        this.username = username;
        this.address = address;
        this.room = room;
        this.connectedAt = connectedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public String getRoom() {
        return room;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }
}
