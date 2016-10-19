package me.hnguyenuml.spyday;

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

import com.konifar.fab_transformation.FabTransformation;

import java.util.ArrayList;
import java.util.List;

import me.hnguyenuml.spyday.Fragments.ListMessageFragment;
import me.hnguyenuml.spyday.UI.SpyDayUtil;

public class SpyDayActivity extends BaseActivity implements
        View.OnClickListener, View.OnTouchListener,
        ListMessageFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private FloatingActionButton mFloatBtn;
    private View rootLayout;
    private View mBottomToolbar;
    private boolean isTransforming;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fix_anim, R.anim.fix_anim);
        setContentView(R.layout.activity_spy_day);

        rootLayout = (View)findViewById(R.id.root_layout);
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
        mBottomToolbar = findViewById(R.id.fab_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.spyViewPager);

        if (mViewPager != null)
            setupViewPager(mViewPager);

        mFloatBtn = (FloatingActionButton) findViewById(R.id.spyFloatBtn);
        mFloatBtn.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        adapter.addFragment(new ListMessageFragment(), "Messages");
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
                if (mFloatBtn.getVisibility() == View.VISIBLE) {
                    FabTransformation.with(mFloatBtn).transformTo(mBottomToolbar);
                    mFloatBtn.setVisibility(View.GONE);
                    isTransforming = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mFloatBtn.getVisibility() != View.VISIBLE && isTransforming) {
            FabTransformation.with(mFloatBtn)
                .setListener(new FabTransformation.OnTransformListener() {
                    @Override
                    public void onStartTransform() {
                        isTransforming = true;
                    }

                    @Override
                    public void onEndTransform() {
                        isTransforming = false;
                    }
                })
                .transformFrom(mBottomToolbar);
        }
        return false;
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
