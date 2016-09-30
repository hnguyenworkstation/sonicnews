package com.greenfam.sonicnews.Content;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by jason on 9/26/16.
 */

public class UserProfile implements Serializable {
    protected  final String TAG = UserProfile.class.getSimpleName();

    private String mUserId;
    private CharSequence displayName;
    private int level;
    private int mNumOfPost;
    private int mNumOfFavoriteFromPost;
    private int mNumOfFollower;
    private int mNumOfFollowing;
    private LatLng mCurrentZone;
    private LatLng mActualCurrentZone;
    private String mUserName;
    private String mUserEmail;
    // private List<Event> mListEventPosted;
    // private List<Comment> mListCommendPosted;

    public UserProfile() {
    }

    public UserProfile(String id, String name, String email) {
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
