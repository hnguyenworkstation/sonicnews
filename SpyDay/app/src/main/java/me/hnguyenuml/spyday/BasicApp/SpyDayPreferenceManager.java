package me.hnguyenuml.spyday.BasicApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.hnguyenuml.spyday.UserContent.User;

/**
 * Created by Hung Nguyen on 10/14/2016.
 */

public class SpyDayPreferenceManager {
    private String TAG = SpyDayPreferenceManager.class.getSimpleName();

    private SharedPreferences mPref;
    private SharedPreferences.Editor mPrefEditor;
    private Context mContext;

    private int PRIVATE_MODE = 0;
    private boolean isLogged;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    private User mUser;

    // Sharedpref file name
    private static final String PREF_NAME = "5p46h@7";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private Location initLocation;

    // Constructor
    public SpyDayPreferenceManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mPrefEditor = mPref.edit();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean isLogged() {
        return false;
    }

    public FirebaseUser getFireBaseUser() {
        return mFirebaseUser;
    }

    public void setFirebaseUser(FirebaseUser user) {
        this.mFirebaseUser = user;
    }

    public void clear() {
        mPrefEditor.clear();
        mPrefEditor.commit();
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public void updateUserByFirebaseUID(String UID) {
        this.mUser = new User();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
    }

    public void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    public Location getInitLocation(){
        return this.initLocation;
    }
}
