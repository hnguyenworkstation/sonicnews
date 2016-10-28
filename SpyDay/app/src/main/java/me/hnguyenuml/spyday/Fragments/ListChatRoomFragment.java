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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import me.hnguyenuml.spyday.ChatRoomActivity;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UI.ChatRoom;
import me.hnguyenuml.spyday.UI.ChatRoomsAdapter;
import me.hnguyenuml.spyday.UI.RecycleViewItemDecoration;

public class ListChatRoomFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
        listChatRoom.add(new ChatRoom("1", "Hung Nguyen", "hello adasdasd",
                Calendar.getInstance().getTime().toString(), 2));
        listChatRoom.add(new ChatRoom("2", "Hung Nguyen", "helloasdasd ",
                Calendar.getInstance().getTime().toString(), 2));
        listChatRoom.add(new ChatRoom("3", "Hung Nguyen", "helloasdafasf",
                Calendar.getInstance().getTime().toString(), 2));
        listChatRoom.add(new ChatRoom("4", "Hung Nguyen", "hello asf afs",
                Calendar.getInstance().getTime().toString(), 2));
        listChatRoom.add(new ChatRoom("5", "Hung Nguyen", "helloa asfasf",
                Calendar.getInstance().getTime().toString(), 2));

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
                startActivity(new Intent(getActivity(), ChatRoomActivity.class));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_message_menu, menu);
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
