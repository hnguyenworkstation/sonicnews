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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.ChatRoomActivity;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UserContent.ChatRoom;
import me.hnguyenuml.spyday.Adapters.ChatRoomsAdapter;
import me.hnguyenuml.spyday.UI.RecycleViewItemDecoration;
import me.hnguyenuml.spyday.UI.WrapContentLinearLayoutManager;

public class ListChatRoomFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    private String TAG = ListChatRoomFragment.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ArrayList<ChatRoom> listChatRoom;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView listRecycleView;
    private SpyDayApplication mInstance;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private final DatabaseReference chatRoomRef = SpyDayApplication.getInstance()
            .getPrefManager().getFirebaseDatabase().child(Endpoint.DB_CHATROOM);

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.list_chatroom_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_chatroom, container, false);
        listRecycleView = (RecyclerView) rootView.findViewById(R.id.chatroom_recycleview);
        mInstance = SpyDayApplication.getInstance();

        listChatRoom = new ArrayList<>();

        mAdapter = new ChatRoomsAdapter(this.getContext(), listChatRoom);
        final WrapContentLinearLayoutManager layoutManager =
                new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        listRecycleView.setLayoutManager(layoutManager);
        listRecycleView.addItemDecoration(new RecycleViewItemDecoration(
                this.getContext()
        ));
        listRecycleView.setItemAnimator(new DefaultItemAnimator());
        listRecycleView.setAdapter(mAdapter);

        listRecycleView.addOnItemTouchListener(new ChatRoomsAdapter
                .RecyclerTouchListener(getContext(), listRecycleView,
                new ChatRoomsAdapter.ClickListener() {
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
                        Toast.makeText(getContext(), "Long Clicked at" + position, Toast.LENGTH_SHORT).show();
                    }
                }));

        fetchChatrooms();

        chatRoomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendNewChatroom(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendNewChatroom(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuchatroom_search:
                return true;
            case R.id.menuchatroom_add:
                try {
                    attempToCreateRoom();
                } catch (Exception e) {

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attempToCreateRoom() {
        String id1 = SpyDayApplication.getInstance()
                .getPrefManager().getFirebaseAuth().getCurrentUser().getUid();
        String id2 = "xw5Ac0tHh4M2ZOLpJrfNBi8hlEF2";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        try {
            String key = chatRoomRef.push().getKey();
            Map<String, Object> mapRoom = new HashMap<>();
            mapRoom.put(Endpoint.DB_CHATROOM_USER1, id1);
            mapRoom.put(Endpoint.DB_CHATROOM_USER2, id2);
            mapRoom.put(Endpoint.DB_CHATROOM_CREATED, sdf.format(cal.getTime()));
            mapRoom.put(Endpoint.DB_CHATROOM_TYPE,Endpoint.TYPE_SINGLE);
            mapRoom.put(Endpoint.DB_CHATROOM_KEY, key);
            chatRoomRef.child(key).updateChildren(mapRoom);
            fetchChatrooms();
            Toast.makeText(getContext(), "Success to create chat room" , Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to create chat room" , Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void fetchChatrooms() {
        chatRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listChatRoom.clear();
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    appendNewChatroom(temp);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void appendNewChatroom(DataSnapshot snapshot) {
        String id1 = snapshot.child(Endpoint.DB_CHATROOM_USER1).getValue().toString();
        String id2 = snapshot.child(Endpoint.DB_CHATROOM_USER2).getValue().toString();
        String key = snapshot.child(Endpoint.DB_CHATROOM_KEY).getValue().toString();
        String time = snapshot.child(Endpoint.DB_CHATROOM_CREATED).getValue().toString();
        String type = snapshot.child(Endpoint.DB_CHATROOM_TYPE).getValue().toString();

        if (mInstance.getPrefManager().getFirebaseAuth().getCurrentUser().getUid().equals(id1)
                    || mInstance.getPrefManager().getFirebaseAuth().getCurrentUser().getUid().equals(id2))
            {
                ChatRoom newcr;
                if (mInstance.getPrefManager().getFirebaseAuth().getCurrentUser().getUid().equals(id1)) {
                    newcr = new ChatRoom(key, id2, "Helloasd", time, 0);
                } else {
                    newcr = new ChatRoom(key, id1, "Helloasd", time, 0);
                }
                listChatRoom.add(newcr);
            }
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
