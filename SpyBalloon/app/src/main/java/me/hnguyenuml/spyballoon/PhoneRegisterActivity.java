package me.hnguyenuml.spyballoon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import me.hnguyenuml.spyballoon.UserContent.AppConfig;

public class PhoneRegisterActivity extends AppCompatActivity {

    private Button phoneVerify;
    private AuthCallback authCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        final TwitterAuthConfig authConfig =  new TwitterAuthConfig(AppConfig.TWITTER_KEY, AppConfig.TWITTER_SECRET);
        Fabric.with(getApplication(), new TwitterCore(authConfig), new Digits.Builder().build());

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
            }
        };

        phoneVerify = (Button) findViewById(R.id.phone_request);
        phoneVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                        .withAuthCallBack(authCallback)
                        .withPhoneNumber("+19786315334");
                Digits.authenticate(authConfigBuilder.build());
            }
        });
    }
}
