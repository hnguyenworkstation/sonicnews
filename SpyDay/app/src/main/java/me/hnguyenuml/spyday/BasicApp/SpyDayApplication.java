package me.hnguyenuml.spyday.BasicApp;

import android.app.Application;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.hnguyenuml.spyday.LoginActivity;

/**
 * Created by Hung Nguyen on 10/20/2016.
 */

public class SpyDayApplication extends Application {
    public static final String TAG = SpyDayApplication.class
            .getSimpleName();

    private static SpyDayApplication mInstance;
    private SpyDayPreferenceManager pref;
    private static final Calendar calendar = Calendar.getInstance();

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

    public String getNow() {
        String now = String.valueOf(calendar.getTime());

        return now;
    }

    public String convertStingToDateFormat(String dateStr) {
        String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";
        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a")
                    : new SimpleDateFormat("dd LLL, hh:mm a");
            String tempdate = format.format(date);
            timestamp = tempdate.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
