package com.greenfam.sonicnews.Content;

/**
 * Created by jason on 9/29/16.
 */

public class ServerEndpoint {
    // localhost url -
    public static final String BASE_URL = "http://hnguyenuml.me/sonicnews/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
