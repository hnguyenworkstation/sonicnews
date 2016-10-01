package com.greenfam.sonicnews;

import com.greenfam.sonicnews.Content.UserProfile;

import java.io.Serializable;

/**
 * Created by jason on 9/22/16.
 */

public class SingleMessage implements Serializable {

    private String fromName, message;
    private String mID;
    private String mTimeStamp;
    private boolean isSelf;
    private String fromID;

    public SingleMessage() {
    }

    public SingleMessage(String id, String message, String createdAt, String userID) {
        this.mID = id;
        this.message = message;
        this.mTimeStamp = createdAt;
        this.fromID = userID;
    }

    public String getId() {
        return mID;
    }

    public void setId(String id) {
        this.mID = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return mTimeStamp;
    }

    public void setCreatedAt(String createdAt) {
        this.mTimeStamp = createdAt;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }
}
