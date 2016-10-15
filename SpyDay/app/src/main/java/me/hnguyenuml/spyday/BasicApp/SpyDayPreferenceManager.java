package me.hnguyenuml.spyday.BasicApp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hung Nguyen on 10/14/2016.
 */

public class SpyDayPreferenceManager {
    private String TAG = SpyDayPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences mPref;
    // Editor for Shared preferences
    SharedPreferences.Editor mPrefEditor;

    // Context
    Context mContext;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    private boolean isLogged;

    // Sharedpref file name
    private static final String PREF_NAME = "5p46h@7";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    // Constructor
    public SpyDayPreferenceManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mPrefEditor = mPref.edit();
    }

    public boolean isLogged() {
        return false;
    }

    public void clear() {
        mPrefEditor.clear();
        mPrefEditor.commit();
    }
}
