package me.hnguyenuml.spyballoon;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import me.hnguyenuml.spyballoon.UserContent.AppConfig;
import me.hnguyenuml.spyballoon.UserContent.SBPreferenceManager;

/**
 * Created by quang on 10/1/2016.
 */

public class SpyBalloonActivity extends AppCompatActivity {
    public static final String TAG = "SPY BALLOON";
    private static SpyBalloonActivity mInstance;
    private AuthCallback authCallback;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SBPreferenceManager sbPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
    }

    public AuthCallback getAuthCallback(){
        return authCallback;
    }

    public SBPreferenceManager getPrefManager() {
        if (sbPref == null) {
            sbPref = new SBPreferenceManager(this);
        }

        return sbPref;
    }
}
