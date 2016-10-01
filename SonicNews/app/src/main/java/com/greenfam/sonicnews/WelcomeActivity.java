package com.greenfam.sonicnews;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends SonicNewsActivity
    implements View.OnClickListener {

    private final int LAST_PAGE = 3;

    private Button skipButton;
    private Button nextButton;
    private ViewPager viewPager;

    private List<View> listView = new ArrayList<>();
    private ImageView[] imageViews = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SonicNewsActivity.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_welcome);

        skipButton = (Button) findViewById(R.id.skipBtn);
        skipButton.setOnClickListener(this);
        nextButton = (Button) findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);

        initUI();
    }

    private void updateButtons() {
        int current = viewPager.getCurrentItem();

        if (current == LAST_PAGE) {
            nextButton.setText("Login");
        } else {
            nextButton.setText("Next");
        }
    }

    private void initUI() {

        listView.add(getLayoutInflater().inflate(R.layout.first_welcome_layout, null));
        listView.add(getLayoutInflater().inflate(R.layout.first_welcome_layout, null));
        listView.add(getLayoutInflater().inflate(R.layout.first_welcome_layout, null));
        listView.add(getLayoutInflater().inflate(R.layout.final_welcome_layout, null));

        imageViews = new ImageView[listView.size()];
        LinearLayout layout = (LinearLayout) findViewById(R.id.welcome_viewgroup);
        for (int i = 0; i < listView.size(); i++) {
            imageViews[i] = new ImageView(WelcomeActivity.this);
            if (0 == i) {
                imageViews[i].setBackgroundResource(R.drawable.ic_point_select);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.ic_point_normal);
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
                        imageViews[i].setBackgroundResource(R.drawable.ic_point_select);
                    } else {
                        imageViews[i].setBackgroundResource(R.drawable.ic_point_normal);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // update buttons when stage changes
                updateButtons();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                int next = viewPager.getCurrentItem() + 1;
                if (next >= 4) {
                    nextButton.setText("Login");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    viewPager.setCurrentItem(next);
                }
                break;
            case R.id.skipBtn:
                viewPager.setCurrentItem(3);
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
