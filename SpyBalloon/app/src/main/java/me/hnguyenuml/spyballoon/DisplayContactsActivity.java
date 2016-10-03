package me.hnguyenuml.spyballoon;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import me.hnguyenuml.spyballoon.Adapters.ContactAdapter;
import me.hnguyenuml.spyballoon.UserContent.SBFriend;
import me.hnguyenuml.spyballoon.UserContent.SBPreferenceManager;

public class DisplayContactsActivity extends SpyBalloonActivity {

    public static final String TAG = DisplayContactsActivity.class
            .getSimpleName();

    private static DisplayContactsActivity mInstance;
    private SBPreferenceManager pref;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<SBFriend> contactList;
    private ArrayList<SBFriend> friendsList;
    private ContactAdapter mFriendAdapter;
    private ContactAdapter mPhoneAdapter;
    private RecyclerView friendRecyclerView;
    private RecyclerView phoneRecyclerView;
    private SBPreferenceManager mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);

        friendRecyclerView = (RecyclerView) findViewById(R.id.friend_list);
        phoneRecyclerView = (RecyclerView) findViewById(R.id.phone_contact_list);

        mPref = new SBPreferenceManager(getBaseContext());

        contactList = mPref.getContactList();
        friendsList = mPref.getContactList();

        mFriendAdapter = new ContactAdapter(getBaseContext(), friendsList);
        mPhoneAdapter = new ContactAdapter(getBaseContext(), contactList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        friendRecyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new SingleMessageDivider(
//                this.getContext()
//        ));
        friendRecyclerView.setItemAnimator(new DefaultItemAnimator());
        friendRecyclerView.setAdapter(mFriendAdapter);

        friendRecyclerView.addOnItemTouchListener(new ContactAdapter.RecyclerTouchListener(
                getBaseContext(), friendRecyclerView, new ContactAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                SBFriend contact = friendsList.get(position);
                Intent intent = new Intent(DisplayContactsActivity.this, MessageActivity.class);
                intent.putExtra("phone", contact.getPhoneNumber());
                intent.putExtra("name", contact.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(getBaseContext());
        phoneRecyclerView.setLayoutManager(secondLayoutManager);
//        recyclerView.addItemDecoration(new SingleMessageDivider(
//                this.getContext()
//        ));
        phoneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        phoneRecyclerView.setAdapter(mPhoneAdapter);

        phoneRecyclerView.addOnItemTouchListener(new ContactAdapter.RecyclerTouchListener(
                getBaseContext(), phoneRecyclerView, new ContactAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                SBFriend contact = contactList.get(position);
                Intent intent = new Intent(DisplayContactsActivity.this, MessageActivity.class);
                intent.putExtra("phone", contact.getPhoneNumber());
                intent.putExtra("name", contact.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
