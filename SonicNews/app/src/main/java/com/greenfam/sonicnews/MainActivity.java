package com.greenfam.sonicnews;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greenfam.sonicnews.Fragments.HomeFragment;
import com.greenfam.sonicnews.Fragments.LocalFragment;
import com.greenfam.sonicnews.Fragments.MessageFragment;
import com.greenfam.sonicnews.Fragments.MoreFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener,
                LocalFragment.OnFragmentInteractionListener,
                MessageFragment.OnFragmentInteractionListener,
                MoreFragment.OnFragmentInteractionListener
{

    private BottomBar bottomBar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {

                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new LocalFragment(), "Local");
        adapter.addFragment(new MessageFragment(), "Message");
        adapter.addFragment(new MoreFragment(), "More");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitle = new ArrayList<>();

        public Context mContext;

        public ViewPagerAdapter(Context mContext, FragmentManager manager) {
            super(manager);
            this.mContext = mContext;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitle.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitle.add(title);
        }
    }
}