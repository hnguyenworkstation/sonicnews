package me.hnguyenuml.spyday;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.BasicApp.SpyDayPreferenceManager;
import me.hnguyenuml.spyday.UI.CircleTransformation;
import me.hnguyenuml.spyday.UI.RevealBackground;
import me.hnguyenuml.spyday.UserContent.User;

public class MyProfileActivity extends BaseActivity implements RevealBackground.OnStateChangeListener {
    private RevealBackground mRevealBackground;

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private RecyclerView mRecyclerView;
    private TabLayout mTabLayout;
    private ImageView mProfilePhoto;
    private View  mProfileDetails;
    private Button mFollowBtn;
    private View mProfileStats;
    private View mProfileRoot;

    private int avatarSize;
    private String profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mRevealBackground = (RevealBackground) findViewById(R.id.myprofile_revealbg);
        mRecyclerView = (RecyclerView) findViewById(R.id.myprofile_recycler);
        mTabLayout = (TabLayout) findViewById(R.id.myprofile_tablayout);
        mProfilePhoto = (ImageView) findViewById(R.id.myprofile_avatar);
        mProfileDetails = findViewById(R.id.myprofile_details);
        mFollowBtn = (Button) findViewById(R.id.myprofile_btn);
        mProfileStats = findViewById(R.id.myprofile_statsview);
        mProfileRoot = findViewById(R.id.myprofile_profileroot);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.profile_circle_image)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mProfilePhoto);

        setupTabs();
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_cast_dark));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_cast_dark));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_cast_dark));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_cast_dark));
    }

    @Override
    public void onStateChange(int state) {

    }
}
