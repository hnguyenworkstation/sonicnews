package me.hnguyenuml.spyday;

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import me.hnguyenuml.spyday.Fragments.EventAroundFragment;
import me.hnguyenuml.spyday.Fragments.ListChatRoomFragment;
import me.hnguyenuml.spyday.UI.SpyDayUtil;

public class SpyDayActivity extends BaseActivity implements
        View.OnClickListener, ListChatRoomFragment.OnFragmentInteractionListener,
        EventAroundFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;

    private View rootLayout;
    private boolean isTransforming;
    private ViewPagerAdapter adapter;


    private FloatingActionButton mFloatBtn;
    private FloatingActionButton fab_1;
    private FloatingActionButton fab_2;
    private FloatingActionButton fab_3;

    private Animation showFabAnim1;
    private Animation closeFabAnim1;
    private Animation showFabAnim2;
    private Animation closeFabAnim2;
    private Animation showFabAnim3;
    private Animation closeFabAnim3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fix_anim, R.anim.fix_anim);
        setContentView(R.layout.activity_spy_day);

        rootLayout = findViewById(R.id.root_layout);
        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);
            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            circularRevealActivity();
                        }

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.spyViewPager);

        if (mViewPager != null)
            setupViewPager(mViewPager);

        mFloatBtn = (FloatingActionButton) findViewById(R.id.spyFloatBtn);
        mFloatBtn.setOnClickListener(this);

        fab_1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab_2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab_3 = (FloatingActionButton) findViewById(R.id.fab_3);

        fab_1.setOnClickListener(this);
        fab_2.setOnClickListener(this);
        fab_3.setOnClickListener(this);

        showFabAnim1 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab1);
        closeFabAnim1 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab1);

        showFabAnim2 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab2);
        closeFabAnim2 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab2);

        showFabAnim3 = AnimationUtils.loadAnimation(getApplication(), R.anim.show_fab3);
        closeFabAnim3 = AnimationUtils.loadAnimation(getApplication(), R.anim.hide_fab3);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        adapter.addFragment(new EventAroundFragment(), "EVAround");
        adapter.addFragment(new ListChatRoomFragment(), "Messages");
        viewPager.setAdapter(adapter);
    }

    private void circularRevealActivity() {
        // make the radius longer to cover the inches
        int cx = rootLayout.getWidth();
        int cy = rootLayout.getHeight();

        float finalRadius = Math.max(rootLayout.getWidth() + 20, rootLayout.getHeight() + 20);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        }
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.spyFloatBtn:
                SpyDayUtil.showToast("Float Clicked", getBaseContext());
                // case float button
                if (isTransforming) {
                    hideFab();
                    isTransforming = false;
                } else {
                    transformFab();
                    isTransforming = true;
                }
                break;
            default:
                break;
        }
    }

    private void transformFab() {
        FrameLayout.LayoutParams layoutParams
                = (FrameLayout.LayoutParams) fab_1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab_1.getWidth() * 1.7);
        layoutParams.bottomMargin +=  (int) (fab_1.getHeight() * 0.25);
        fab_1.setLayoutParams(layoutParams);
        fab_1.setClickable(true);
        fab_1.startAnimation(showFabAnim1);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab_2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab_2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab_2.getHeight() * 1.5);
        fab_2.setLayoutParams(layoutParams2);
        fab_2.startAnimation(showFabAnim2);
        fab_2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab_3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab_3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab_3.getHeight() * 1.7);
        fab_3.setLayoutParams(layoutParams3);
        fab_3.startAnimation(showFabAnim3);
        fab_3.setClickable(true);
    }

    private void hideFab() {
        FrameLayout.LayoutParams layoutParams
                = (FrameLayout.LayoutParams) fab_1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab_1.getWidth() * 1.7);
        layoutParams.bottomMargin -=  (int) (fab_1.getHeight() * 0.25);
        fab_1.setLayoutParams(layoutParams);
        fab_1.setClickable(true);
        fab_1.startAnimation(closeFabAnim1);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab_2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab_2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab_2.getHeight() * 1.5);
        fab_2.setLayoutParams(layoutParams2);
        fab_2.startAnimation(closeFabAnim2);
        fab_2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab_3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab_3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab_3.getHeight() * 1.7);
        fab_3.setLayoutParams(layoutParams3);
        fab_3.startAnimation(closeFabAnim3);
        fab_3.setClickable(false);

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
