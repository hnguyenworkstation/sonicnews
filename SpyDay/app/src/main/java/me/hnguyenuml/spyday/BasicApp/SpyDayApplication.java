package me.hnguyenuml.spyday.BasicApp;

import android.app.Application;
import android.content.Intent;

import me.hnguyenuml.spyday.LoginActivity;

/**
 * Created by Hung Nguyen on 10/20/2016.
 */

public class SpyDayApplication extends Application {
    public static final String TAG = SpyDayApplication.class
            .getSimpleName();

    private static SpyDayApplication mInstance;
    private SpyDayPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = SpyDayApplication.this;
    }

    public static synchronized SpyDayApplication getInstance() {
        return mInstance;
    }

    public SpyDayPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new SpyDayPreferenceManager(this);
        }
        return pref;
    }

    public void logout() {
        pref.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
