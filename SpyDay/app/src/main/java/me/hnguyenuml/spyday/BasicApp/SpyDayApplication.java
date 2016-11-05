package me.hnguyenuml.spyday.BasicApp;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.hnguyenuml.spyday.LoginActivity;
import me.hnguyenuml.spyday.Utils.LruBitmapCache;

/**
 * Created by Hung Nguyen on 10/20/2016.
 */

public class SpyDayApplication extends Application {
    public static final String TAG = SpyDayApplication.class
            .getSimpleName();

    private static SpyDayApplication mInstance;
    private SpyDayPreferenceManager pref;
    private static final Calendar calendar = Calendar.getInstance();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

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

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public void logout() {
        pref.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public String getNow() {
        String now = String.valueOf(calendar.getTime());

        return now;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
