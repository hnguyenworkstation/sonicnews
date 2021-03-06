package com.greenfam.sonicnews;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenfam.sonicnews.Fragments.HomeFragment;
import com.greenfam.sonicnews.Fragments.HomeHotNewsFragment;
import com.greenfam.sonicnews.Fragments.HomeNewNewsFragment;
import com.greenfam.sonicnews.Fragments.HomeTrendingNewsFragment;
import com.greenfam.sonicnews.Fragments.LocalFragment;
import com.greenfam.sonicnews.Fragments.MessageFragment;
import com.greenfam.sonicnews.Fragments.MoreFragment;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SonicNewsActivity
        implements
                HomeFragment.OnFragmentInteractionListener,
                LocalFragment.OnFragmentInteractionListener,
                MessageFragment.OnFragmentInteractionListener,
                MoreFragment.OnFragmentInteractionListener,
                HomeHotNewsFragment.OnFragmentInteractionListener,
                HomeTrendingNewsFragment.OnFragmentInteractionListener,
                HomeNewNewsFragment.OnFragmentInteractionListener
{
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter adapter;
    private TextView tittleTextView;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private int[] mTabsIcons = {
            R.drawable.ic_favorite_white_24dp,
            R.drawable.ic_location_on_white_24dp,
            R.drawable.ic_update_white_24dp,
            R.drawable.ic_local_dining_white_24dp
    };

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tittleTextView = (TextView) findViewById(R.id.main_title);
        actionBar = getSupportActionBar();

        activateToolbar();

        if (viewPager != null)
            setupViewPager(viewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(adapter.getTabView(i));
            }

            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new LocalFragment(), "Local");
        adapter.addFragment(new MessageFragment(), "Message");
        adapter.addFragment(new MoreFragment(), "More");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    protected void activateToolbar() {
        if (toolbar == null)
            toolbar = (Toolbar) findViewById(R.id.app_bar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // toolbar.setLogo(R.drawable.ic_favorite_white_24dp);
            getSupportActionBar().setTitle("Sonic News");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitle = new ArrayList<>();

        public Context mContext;

        public ViewPagerAdapter(Context mContext, FragmentManager manager) {
            super(manager);
            this.mContext = mContext;
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(mFragmentTitle.get(position));
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
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