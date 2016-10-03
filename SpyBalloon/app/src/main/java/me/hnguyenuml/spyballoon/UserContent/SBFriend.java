package me.hnguyenuml.spyballoon.UserContent;

import java.io.Serializable;

/**
 * Created by jason on 10/3/16.
 */

public class SBFriend implements Serializable {
    private final String TAG = SBFriend.class.getSimpleName();
    private String accountID;
    private String name;
    private String phoneNumber;
    private boolean isJoined;

    public SBFriend(String id, String name, String phone, boolean isJoined) {
        this.accountID = id;
        this.name = name;
        this.phoneNumber = phone;
        this.isJoined = isJoined;
    }

    public SBFriend(String name, String phone, boolean isJoined) {
        this.name = name;
        this.phoneNumber = phone;
        this.isJoined = isJoined;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }
}
