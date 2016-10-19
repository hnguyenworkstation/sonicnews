package me.hnguyenuml.spyday;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.net.Uri;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;

import me.hnguyenuml.spyday.Fragments.GetNameFragment;
import me.hnguyenuml.spyday.Fragments.GetProfilePictureFragment;
import me.hnguyenuml.spyday.Fragments.LoginFragment;
import me.hnguyenuml.spyday.Fragments.RegisterFragment;

public class LoginActivity extends BaseActivity
        implements
            OnClickListener,
            RegisterFragment.OnFragmentInteractionListener,
            LoginFragment.OnFragmentInteractionListener,
            GetNameFragment.OnFragmentInteractionListener,
            GetProfilePictureFragment.OnFragmentInteractionListener {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button registerBtn;
    private Fragment loginFragment;
    private FragmentManager mFragManager;
    private FragmentTransaction mFragTransition;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        overridePendingTransition(R.anim.fix_anim, R.anim.fix_anim);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        mFragManager = getSupportFragmentManager();
        mFragTransition = mFragManager.beginTransaction();

        mFragTransition.replace(R.id.contentFragment, loginFragment);
        mFragTransition.commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegisterFragment(), "Register");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        registerBtn = (Button) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                viewPager.setCurrentItem(1, true);
                System.out.println("pressed");
                return;
            default:
                return;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<CharSequence> mFragmentTitle = new ArrayList<>();

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

