package me.hnguyenuml.spyday.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.BasicApp.SpyDayPreferenceManager;
import me.hnguyenuml.spyday.ChatRoomActivity;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UI.ChatRoom;
import me.hnguyenuml.spyday.UI.ChatRoomsAdapter;
import me.hnguyenuml.spyday.UI.RecycleViewItemDecoration;

public class ListChatRoomFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";


    private String TAG = ListChatRoomFragment.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ArrayList<ChatRoom> listChatRoom;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView listRecycleView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListChatRoomFragment() {
    }

    public static ListChatRoomFragment newInstance(String param1, String param2) {
        ListChatRoomFragment fragment = new ListChatRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_chatroom, container, false);
        listRecycleView = (RecyclerView) rootView.findViewById(R.id.chatroom_recycleview);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.listchatroom_toolbar);
        AppCompatActivity tempActivity = (AppCompatActivity) this.getActivity();
        tempActivity.setSupportActionBar(toolbar);
        tempActivity.setTitle("Messages");

        listChatRoom = new ArrayList<>();

        mAdapter = new ChatRoomsAdapter(this.getContext(), listChatRoom);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecycleView.setLayoutManager(layoutManager);
        listRecycleView.addItemDecoration(new RecycleViewItemDecoration(
                this.getContext()
        ));
        listRecycleView.setItemAnimator(new DefaultItemAnimator());
        listRecycleView.setAdapter(mAdapter);

        listRecycleView.addOnItemTouchListener(new ChatRoomsAdapter.
                RecyclerTouchListener(this.getContext(),
                listRecycleView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatRoom chatRoom = listChatRoom.get(position);
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_chatroom_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuchatroom_search:
                return true;
            case R.id.menuchatroom_add:
                attempToCreateRoom();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attempToCreateRoom() {
        String id1 = SpyDayApplication.getInstance()
                .getPrefManager().getFirebaseAuth().getCurrentUser().getUid();
        String id2 = "xw5Ac0tHh4M2ZOLpJrfNBi8hlEF2";

        // concat two ids
        String newID = id1 + id2;

        DatabaseReference chatRef = SpyDayApplication.getInstance()
                .getPrefManager().getFirebaseDatabase();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        try {
            chatRef = chatRef.child(Endpoint.DB_CHATROOM);
            chatRef.child(newID).child(Endpoint.DB_CHATROOM_CREATED).setValue(sdf.format(cal.getTime()));
            chatRef.child(newID).child(Endpoint.DB_CHATROOM_TYPE).setValue(Endpoint.TYPE_SINGLE);
            chatRef.child(newID).child(Endpoint.DB_CHATROOM_USER1)
                    .setValue(id1);
            chatRef.child(newID).child(Endpoint.DB_CHATROOM_USER2)
                    .setValue(id2);
            chatRef.push();

            pushUserChatroom(id1, id2, newID);

            Toast.makeText(getContext(), "Success to create chat room" , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to create chat room" , Toast.LENGTH_SHORT).show();
            return;
        }

        // If no error found
        fetchChatRooms();
    }

    private void fetchChatRooms() {
        DatabaseReference userRef = SpyDayApplication.getInstance().getPrefManager().getUserDatabase();

        userRef.child(SpyDayApplication.getInstance().getPrefManager().getUser().getUserUID())
                .child(Endpoint.USER_AVAILABLE_CHATROOM)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ChatRoom newcr = new ChatRoom(ds.getValue().toString());
                    newcr.setName(ds.getKey());
                    listChatRoom.add(newcr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pushUserChatroom(String person1ID, String person2ID, String chatroomID) {
        // update current user
        DatabaseReference userRef = SpyDayApplication.getInstance().getPrefManager()
                .getUserDatabase();
        userRef = userRef.child(SpyDayApplication.getInstance().getPrefManager()
                .getFirebaseAuth().getCurrentUser().getUid());
        userRef.child(Endpoint.USER_AVAILABLE_CHATROOM)
                .child(person2ID)
                .child(Endpoint.CHATROOM_ID)
                .setValue(chatroomID);
        userRef.push();

        // now update user 2
        userRef = SpyDayApplication.getInstance().getPrefManager()
                .getUserDatabase();
        userRef = userRef.child(person2ID);
        userRef.child(Endpoint.USER_AVAILABLE_CHATROOM)
                .child(person1ID)
                .child(Endpoint.CHATROOM_ID)
                .setValue(chatroomID);
        userRef.push();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
