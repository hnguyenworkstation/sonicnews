package me.hnguyenuml.spyday.Static;

/**
 * Created by jason on 10/27/16.
 */

public class Endpoint {
    // Users
    public static final String DB_USER = "Users";
    public static final String USER_AVAILABLE_CHATROOM = "available_chatrooms";
    public static final String USER_NICKNAME = "userNickName";
    public static final String USER_NAME = "userName";

    // Chat Rooms
    public static final String DB_CHATROOM = "Chatrooms";
    public static final String DB_CHATROOM_USER1 = "participant1";
    public static final String DB_CHATROOM_USER2 = "participant2";
    public static final String DB_CHATROOM_CREATED = "time_created";
    public static final String DB_CHATROOM_TYPE = "type";
    public static final String DB_CHATROOM_LISTMESSAGES = "list_messages";

    public static final String CHATROOM_ID = "chatroom_id";
    public static final String CHATROOM_LASTMESSAGE = "";

    public static final String TYPE_SINGLE = "single";
    public static final String TYPE_GROUP = "group";

    // Event
    public static final String DB_EVENT = "Events";
    public static final String EVENT_TITLE = "evTitle";
    public static final String EVENT_DESC = "evDesc";
    public static final String EVENT_ID = "evUID";
    public static final String EVENT_CREATED_AT = "evCreated";
}
