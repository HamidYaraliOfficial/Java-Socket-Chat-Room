package com.chatroom.net;

/**
 * Every kind of message exchanged between client and server over the
 * socket connection. A single {@link ChatMessage} class carries a field
 * of this type so both sides know how to interpret the payload.
 */
public enum MessageType {
    /** Client -> Server: request to join with a username and room. */
    JOIN_REQUEST,
    /** Server -> Client: join accepted. */
    JOIN_ACCEPTED,
    /** Server -> Client: join rejected (e.g. username taken). */
    JOIN_REJECTED,
    /** Client -> Server: broadcast a chat message to the current room. */
    CHAT,
    /** Client -> Server: send a private message to a specific user. */
    PRIVATE_CHAT,
    /** Server -> Client: someone joined the room. */
    USER_JOINED,
    /** Server -> Client: someone left the room. */
    USER_LEFT,
    /** Server -> Client: updated list of users currently in the room. */
    USER_LIST,
    /** Client -> Server: switch to (and create if needed) a different room. */
    CHANGE_ROOM,
    /** Server -> Client: administrative notice/announcement. */
    SERVER_NOTICE,
    /** Server -> Client: this client has been kicked by the operator. */
    KICKED,
    /** Client -> Server, or Server -> Client: keep-alive ping. */
    PING
}
