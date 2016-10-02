package me.hnguyenuml.spyballoon;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.hnguyenuml.spyballoon.UserContent.SBPreferenceManager;

/**
 * Created by quang on 10/1/2016.
 */

public class SpyBalloonActivity extends AppCompatActivity {
    public static final String TAG = "SPY BALLOON";
    private static SpyBalloonActivity mInstance;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SBPreferenceManager sbPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
    }

    public SBPreferenceManager getPrefManager() {
        if (sbPref == null) {
            sbPref = new SBPreferenceManager(this);
        }

        return sbPref;
    }
}
