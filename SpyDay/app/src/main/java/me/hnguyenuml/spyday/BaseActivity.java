package me.hnguyenuml.spyday;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;


/**
 * Created by Hung Nguyen on 10/23/2016.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "SPYDAY";
    private static BaseActivity mInstance;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
    }

}
