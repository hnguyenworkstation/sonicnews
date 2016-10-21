package me.hnguyenuml.spyday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView mSplashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSplashText = (TextView) findViewById(R.id.splash_text);
        final Animation anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate_anim);
        final Animation anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out_to_bottom);

        mSplashText.setAnimation(anim1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashText.setAnimation(anim2);
                Intent i = new Intent(SplashScreenActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
