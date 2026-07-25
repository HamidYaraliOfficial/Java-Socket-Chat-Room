package com.chatroom.net;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The single wire-format object sent between client and server via
 * {@link java.io.ObjectOutputStream}/{@link java.io.ObjectInputStream}.
 * Not every field is used by every {@link MessageType} - see the
 * constructors below for the intended combinations.
 */
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private String sender;
    private String recipient;
    private String room;
    private String content;
    private LocalDateTime timestamp;
    private List<String> userList;

    public ChatMessage(MessageType type) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public static ChatMessage joinRequest(String username, String room) {
        ChatMessage m = new ChatMessage(MessageType.JOIN_REQUEST);
        m.sender = username;
        m.room = room;
        return m;
    }

    public static ChatMessage chat(String sender, String room, String content) {
        ChatMessage m = new ChatMessage(MessageType.CHAT);
        m.sender = sender;
        m.room = room;
        m.content = content;
        return m;
    }

    public static ChatMessage privateChat(String sender, String recipient, String content) {
        ChatMessage m = new ChatMessage(MessageType.PRIVATE_CHAT);
        m.sender = sender;
        m.recipient = recipient;
        m.content = content;
        return m;
    }

    public static ChatMessage changeRoom(String room) {
        ChatMessage m = new ChatMessage(MessageType.CHANGE_ROOM);
        m.room = room;
        return m;
    }

    public static ChatMessage serverNotice(String content) {
        ChatMessage m = new ChatMessage(MessageType.SERVER_NOTICE);
        m.content = content;
        return m;
    }

    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
