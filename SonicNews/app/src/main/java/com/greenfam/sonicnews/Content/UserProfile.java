package com.greenfam.sonicnews.Content;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jason on 9/26/16.
 */

public class UserProfile {
    protected  final String TAG = UserProfile.class.getSimpleName();

    private int mUserId;
    private CharSequence displayName;
    private int level;
    private int mNumOfPost;
    private int mNumOfFavoriteFromPost;
    private int mNumOfFollower;
    private int mNumOfFollowing;
    private LatLng mCurrentZone;
    private LatLng mActualCurrentZone;
    // private List<Event> mListEventPosted;
    // private List<Comment> mListCommendPosted;

}
