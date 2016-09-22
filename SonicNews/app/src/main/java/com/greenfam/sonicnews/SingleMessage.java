package com.greenfam.sonicnews;

/**
 * Created by jason on 9/22/16.
 */

public class SingleMessage {

    private String fromName, message;
    private boolean isSelf;

    public SingleMessage() {
    }

    public SingleMessage(String fromName, String message, boolean isSelf) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }
}
