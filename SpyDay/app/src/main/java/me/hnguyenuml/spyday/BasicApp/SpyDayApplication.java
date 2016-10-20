package me.hnguyenuml.spyday.BasicApp;

import android.app.Application;

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
        mInstance = this;
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
}
