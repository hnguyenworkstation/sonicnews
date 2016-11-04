package me.hnguyenuml.spyday.BasicApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.fitness.data.Application;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
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

    private User mUser;

    // Sharedpref file name
    private static final String PREF_NAME = "5p46h@7";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private FirebaseAuth mFirebaseAuth;
    private StorageReference mFirebaseStorage;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mUserDatabase;
    private Location initLocation;

    public SpyDayPreferenceManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mPrefEditor = mPref.edit();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference(Endpoint.DB_USER);
        mUser = new User();
    }

    public DatabaseReference getUserDatabase() {
        return mUserDatabase;
    }

    public DatabaseReference getFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public StorageReference getFirebaseStorage() {
        return mFirebaseStorage;
    }

    public boolean isLogged() {
        return false;
    }

    public void clear() {
        mPrefEditor.clear();
        mPrefEditor.commit();
    }

    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }

    public String getCurrentUID() {
        return mFirebaseAuth.getCurrentUser().getUid();
    }

    public void updateUserByFirebaseUID() {
        this.mUser = new User(mFirebaseAuth.getCurrentUser().getUid());
    }

    public User getUser() {
        return mUser;
    }

    public void storeUser(User mUser) {
        this.mUser = mUser;
    }

    public void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    public Location getInitLocation(){
        return this.initLocation;
    }
}
