package me.hnguyenuml.spyballoon.UserContent;

import java.io.Serializable;

/**
 * Created by quang on 10/1/2016.
 */

public class SBUser implements Serializable {
    protected  final String TAG = SBUser.class.getSimpleName();

    private String mUserId;
    private CharSequence displayName;
    private int level;
    private int mNumOfPosts;
    private int mNumOfFavoriteFromPosts;
    private int mNumOfFollowers;
    private int mNumOfFollowing;
    private String mUserName;
    private String mUserEmail;

    public SBUser() {
    }

    public SBUser(String id, String name, String email) {
        this.mUserId = id;
        this.mUserName = name;
        this.mUserEmail = email;
    }

    public String getId() {
        return mUserId;
    }

    public void setId(String id) {
        this.mUserId = id;
    }

    public String getName() {
        return mUserName;
    }

    public void setName(String name) {
        this.mUserEmail = name;
    }

    public String getEmail() {
        return mUserEmail;
    }

    public void setEmail(String email) {
        this.mUserEmail = email;
    }
}
