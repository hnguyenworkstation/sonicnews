package me.hnguyenuml.spyday;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SpyDayActivity extends BaseActivity {
    private ViewPager mViewPager;
    private FloatingActionButton mFloatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spy_day);

        mViewPager = (ViewPager) findViewById(R.id.spyViewPager);
        mFloatBtn = (FloatingActionButton) findViewById(R.id.spyFloatBtn);
    }
}
