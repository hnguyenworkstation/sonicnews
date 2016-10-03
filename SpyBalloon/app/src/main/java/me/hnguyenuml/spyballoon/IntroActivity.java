package me.hnguyenuml.spyballoon;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends SpyBalloonActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private List<View> listView = new ArrayList<>();
    private ImageView[] imageViews = null;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        loginButton = (Button) findViewById(R.id.intro_login_btn);
        loginButton.setOnClickListener(this);
        registerButton = (Button) findViewById(R.id.intro_register_btn);
        registerButton.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.intro_viewpager);

        initUI();
    }

    private void initUI() {
        listView.add(getLayoutInflater().inflate(R.layout.intro_slice_1, null));
        listView.add(getLayoutInflater().inflate(R.layout.intro_slice_1, null));
        listView.add(getLayoutInflater().inflate(R.layout.intro_slice_1, null));
        listView.add(getLayoutInflater().inflate(R.layout.intro_slice_1, null));

        imageViews = new ImageView[listView.size()];
        LinearLayout layout = (LinearLayout) findViewById(R.id.welcome_viewgroup);
        for (int i = 0; i < listView.size(); i++) {
            imageViews[i] = new ImageView(IntroActivity.this);
            if (0 == i) {
                imageViews[i].setBackgroundResource(R.drawable.ic_active_dot);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.ic_inactive_dot);
            }
            imageViews[i].setPadding(10, 10, 10, 10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(10);
            lp.setMarginEnd(10);
            imageViews[i].setLayoutParams(lp);
            layout.addView(imageViews[i]);
        }

        viewPager.setAdapter(new ViewPagerAdapter(listView));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < listView.size(); i++) {
                    if (position == i) {
                        imageViews[i].setBackgroundResource(R.drawable.ic_active_dot);
                    } else {
                        imageViews[i].setBackgroundResource(R.drawable.ic_inactive_dot);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // update buttons when stage changes
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.intro_login_btn:
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.intro_register_btn:
                startActivity(new Intent(IntroActivity.this, PhoneRegisterActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    class ViewPagerAdapter extends PagerAdapter {
        private List<View> list = null;

        public ViewPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
