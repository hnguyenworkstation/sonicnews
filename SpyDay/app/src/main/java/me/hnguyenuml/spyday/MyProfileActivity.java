package me.hnguyenuml.spyday;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import me.hnguyenuml.spyday.Adapters.UserProfileAdapter;
import me.hnguyenuml.spyday.Adapters.ViewPagerAdapter;
import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.BasicApp.SpyDayPreferenceManager;
import me.hnguyenuml.spyday.Fragments.EventAroundFragment;
import me.hnguyenuml.spyday.Fragments.ListChatRoomFragment;
import me.hnguyenuml.spyday.Fragments.ProfileImageTabFragment;
import me.hnguyenuml.spyday.UI.CircleTransformation;
import me.hnguyenuml.spyday.UI.RevealBackground;
import me.hnguyenuml.spyday.UserContent.User;

public class MyProfileActivity extends BaseActivity
        implements RevealBackground.OnStateChangeListener,
            ProfileImageTabFragment.OnFragmentInteractionListener {
    private RevealBackground mRevealBackground;

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private TabLayout mTabLayout;
    private ImageView mProfilePhoto;
    private View  mProfileDetails;
    private Button mFollowBtn;
    private View mProfileStats;
    private View mProfileRoot;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    private int avatarSize;
    private String profilePhoto;
    private final ProfileImageTabFragment mImageTab = new ProfileImageTabFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mRevealBackground = (RevealBackground) findViewById(R.id.myprofile_revealbg);
        mTabLayout = (TabLayout) findViewById(R.id.myprofile_tablayout);
        mProfilePhoto = (ImageView) findViewById(R.id.myprofile_avatar);
        mProfileDetails = findViewById(R.id.myprofile_details);
        mFollowBtn = (Button) findViewById(R.id.myprofile_btn);
        mProfileStats = findViewById(R.id.myprofile_statsview);
        mProfileRoot = findViewById(R.id.myprofile_profileroot);
        mViewPager = (ViewPager) findViewById(R.id.myprofile_viewpager);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.profile_circle_image)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mProfilePhoto);

        if (mViewPager != null)
            setupViewPager(mViewPager);
        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(this.getBaseContext(), getSupportFragmentManager());
        mViewPagerAdapter.addFragment(mImageTab, "TabImage");
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    public static void startUserProfileFromLocation(int[] startingLocation, Activity activity) {
        Intent intent = new Intent(activity, MyProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        activity.startActivity(intent);
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_cast_dark));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupUserProfileGrid() {

    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            mRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mRevealBackground.setToFinishedFrame();
        }
    }


    @Override
    public void onStateChange(int state) {
        if (RevealBackground.STATE_FINISHED == state) {
            mImageTab.setVisualizeView(View.VISIBLE);
            mTabLayout.setVisibility(View.VISIBLE);
            mProfileRoot.setVisibility(View.VISIBLE);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            mTabLayout.setVisibility(View.INVISIBLE);
            mImageTab.setVisualizeView(View.INVISIBLE);
            mProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        mTabLayout.setTranslationY(-mTabLayout.getHeight());
        mTabLayout.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
        mProfileRoot.setTranslationY(-mProfileRoot.getHeight());
        mProfilePhoto.setTranslationY(-mProfilePhoto.getHeight());
        mProfileDetails.setTranslationY(-mProfileDetails.getHeight());
        mProfileStats.setAlpha(0);

        mProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        mProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        mProfileDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        mProfileStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
