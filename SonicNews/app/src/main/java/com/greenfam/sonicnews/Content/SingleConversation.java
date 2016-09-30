package com.greenfam.sonicnews.Content;

import java.io.Serializable;

/**
 * Created by jason on 9/29/16.
 */

public class SingleConversation implements Serializable {
    private String mID;
    private String mFromName;
    private String mLastMessage;
    private String mTimestamp;
    int unreadCount;

    public SingleConversation() {
    }

    public SingleConversation(String id, String name, String lastMessage, String timestamp, int unreadCount) {
        this.mID = id;
        this.mFromName = name;
        this.mLastMessage = lastMessage;
        this.mTimestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return mID;
    }

    public void setId(String id) {
        this.mID = id;
    }

    public String getName() {
        return mFromName;
    }

    public void setName(String name) {
        this.mFromName = name;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.mLastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        this.mTimestamp = timestamp;
    }
}
