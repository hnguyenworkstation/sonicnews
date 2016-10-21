package me.hnguyenuml.spyday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends BaseActivity {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileNickname;
    private TextView profileUID;
    private TextView profileJoinDate;
    private TextView profileCurrentLoc;
    private TextView profileCurrentVisitLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        profileImage = (CircleImageView) findViewById(R.id.myprofile_image);
        profileName = (TextView) findViewById(R.id.myprofile_name);
        profileNickname = (TextView) findViewById(R.id.myprofile_nickname);
        profileUID = (TextView) findViewById(R.id.myprofile_uid);
        profileJoinDate = (TextView) findViewById(R.id.myprofile_joindate);
        profileCurrentLoc = (TextView) findViewById(R.id.myprofile_currentLoc);
        profileCurrentVisitLoc = (TextView) findViewById(R.id.myprofile_currentVisitLoc);
    }
}
