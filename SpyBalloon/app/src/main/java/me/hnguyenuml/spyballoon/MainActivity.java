package me.hnguyenuml.spyballoon;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import me.hnguyenuml.spyballoon.Fragments.ChatFragment;
import me.hnguyenuml.spyballoon.Fragments.HomeFragment;
import me.hnguyenuml.spyballoon.UserContent.AppConfig;
import me.hnguyenuml.spyballoon.UserContent.SBFriend;
import me.hnguyenuml.spyballoon.UserContent.SBPreferenceManager;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener{

    private FragmentManager fragmentList;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private int currentState;
    private SBPreferenceManager mPref;

    private final int HOME_STATE = 1;
    private final int AROUND_STATE = 2;
    private final int CHAT_STATE = 3;
    private final int MORE_STATE = 4;

    private String HOME_TITLE;
    private String CHAT_TITLE;
    private String AROUND_TITLE;
    private Menu mMenu;

    private static final int REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(AppConfig.TWITTER_KEY, AppConfig.TWITTER_SECRET);
        Fabric.with(this, new Digits.Builder().build(), new Twitter(authConfig));

        HOME_TITLE = getString(R.string.home_title);
        CHAT_TITLE = getString(R.string.chat_title);
        AROUND_TITLE = getString(R.string.around_title);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // getting preference
        mPref = new SBPreferenceManager(getBaseContext());

        // Draw nav
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // could ask for contact
        mayRequestContacts();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Getting fragment manager
        fragmentList = getFragmentManager();

        // Default display
        showHomeFragment();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, DisplayContactsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.chat_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showHomeFragment();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_message) {
            showChatFragment();
            getMenuInflater().inflate(R.menu.message_menu, mMenu);
            fab.setImageResource(R.drawable.ic_cast_white);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChatFragment() {
        fragmentList.beginTransaction().replace(R.id.frame_content, new ChatFragment()).commit();
        toolbar.setTitle(getString(R.string.chat_title));
    }

    private void showHomeFragment() {
        fragmentList.beginTransaction().replace(R.id.frame_content, new HomeFragment()).commit();
        toolbar.setTitle(getString(R.string.home_title));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(null, R.string.request_contact_permission, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(View v) {
                        requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                    }
                });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Todo: getting list contact here
                collectAllContacts();
            }
        }
    }

    private void collectAllContacts() {
        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        ArrayList<SBFriend> contactList = new ArrayList<>();

        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));

            contactList.add(new SBFriend(name, phoneNumber, false));
        }

        phones.close();
        mPref.setContactList(contactList);
    }
}
