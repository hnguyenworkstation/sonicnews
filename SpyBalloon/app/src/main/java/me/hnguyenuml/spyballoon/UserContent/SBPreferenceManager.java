package me.hnguyenuml.spyballoon.UserContent;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by quang on 10/1/2016.
 */

public class SBPreferenceManager {

    private String TAG = SBPreferenceManager.class.getSimpleName();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context mContext;
    private final int PRIVATE_MODE = 0;

    private static final String SB_PREF_NAME = "spyballoon_firebase";
    private static final String SB_KEY_USER_NAME = "user_name";
    private static final String SB_KEY_USER_EMAIL = "user_email";
    private static final String SB_KEY_USER_ID = "user_id";
    private static final String SB_KEY_NOTIFICATIONS = "notifications";

    private ArrayList<SBFriend> contactList;

    public SBPreferenceManager(Context context) {
        this.mContext = context;
        pref = context.getSharedPreferences(SB_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeUser(SBUser user) {
        editor.putString(SB_KEY_USER_ID, user.getId());
        editor.putString(SB_KEY_USER_EMAIL, user.getName());
        editor.putString(SB_KEY_USER_ID, user.getEmail());
        editor.commit();
        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public SBUser getUser() {
        if (pref.getString(SB_KEY_USER_ID, null) != null) {
            String id, name, email;
            id = pref.getString(SB_KEY_USER_ID, null);
            name = pref.getString(SB_KEY_USER_NAME, null);
            email = pref.getString(SB_KEY_USER_EMAIL, null);
            SBUser user = new SBUser(id, name, email);
            return user;
        }
        return null;
    }

    public void addNotification(String notification) {
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(SB_KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(SB_KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public ArrayList<SBFriend> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<SBFriend> list) {
        this.contactList = list;
    }
}
