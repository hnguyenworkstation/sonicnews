package me.hnguyenuml.spyday.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.hnguyenuml.spyday.Adapters.EventAdapter;
import me.hnguyenuml.spyday.AddEventActivity;
import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UI.CustomScrollingHandler;
import me.hnguyenuml.spyday.UI.WrapContentLinearLayoutManager;
import me.hnguyenuml.spyday.UserContent.Event;

public class EventAroundFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = EventAroundFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private EventAdapter listAdapter;
    private List<Event> feedItems;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefLayout;

    private final SpyDayApplication mInstance = SpyDayApplication.getInstance();
    private final DatabaseReference mRootRef = mInstance.getPrefManager()
            .getFirebaseDatabase().child(Event.EVENT_DATABASE);

    private final int duration = 1000;
    private WrapContentLinearLayoutManager mLayoutManager;

    public EventAroundFragment() {

    }

    public static EventAroundFragment newInstance(String param1, String param2) {
        EventAroundFragment fragment = new EventAroundFragment();
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

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event_around, container, false);

        initUI(rootView);

        return rootView;
    }

    private void initUI(View rootView) {
        mLayoutManager = new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.evaround_recyclerview);

        feedItems = new ArrayList<>();
        listAdapter = new EventAdapter(getActivity(), feedItems);
        listAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerView.scrollToPosition(positionStart);
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(listAdapter);

        mSwipeRefLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.evaround_swiperef);
        mSwipeRefLayout.setColorSchemeResources(R.color.orange1, R.color.green1, R.color.blue1);
        mSwipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchLocalEvent();
                        mSwipeRefLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.child(Event.EVENT_ID).getValue().toString();
                String image = null;
                if (dataSnapshot.child(Event.EVENT_ID).getValue() != null)
                    image = dataSnapshot.child(Event.EVENT_IMAGE).getValue().toString();
                String name = dataSnapshot.child(Event.EVENT_NAME).getValue().toString();
                String profilePic = dataSnapshot.child(Event.EVENT_PROFILEPIC).getValue().toString();
                String status = dataSnapshot.child(Event.EVENT_STATUS).getValue().toString();
                String timeStamp = dataSnapshot.child(Event.EVENT_TIMESTAMP).getValue().toString();
                String url = dataSnapshot.child(Event.EVENT_URL).getValue().toString();
                int likeCount = Integer.parseInt(dataSnapshot.child(Event.EVENT_LIKECOUNT).getValue().toString());
                int commentCount = Integer.parseInt(dataSnapshot.child(Event.EVENT_COMMENTCOUNT).getValue().toString());

                Event event = new Event(id, name, image, status,
                        profilePic, timeStamp, url, likeCount, commentCount);
                event.setType(Event.TYPE_IMAGE_EVENT);
                feedItems.add(event);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
    }

    private void fetchLocalEvent() {
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedItems.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String id = ds.child("id").getValue().toString();
                    String image = null;
                    if (ds.child("image").getValue() != null)
                        image = ds.child("image").getValue().toString();
                    String name = ds.child("name").getValue().toString();
                    String profilePic = ds.child("profilePic").getValue().toString();
                    String status = ds.child("status").getValue().toString();
                    String timeStamp = ds.child("timeStamp").getValue().toString();
                    String url = ds.child("url").getValue().toString();
                    int likeCount = Integer.parseInt(ds.child(Event.EVENT_LIKECOUNT).getValue().toString());
                    int commentCount = Integer.parseInt(ds.child(Event.EVENT_COMMENTCOUNT).getValue().toString());

                    Event event = new Event(id, name, image, status,
                            profilePic, timeStamp, url, likeCount, commentCount);
                    event.setType(Event.TYPE_IMAGE_EVENT);
                    feedItems.add(event);

                    listAdapter.notifyDataSetChanged();
                }

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(0);
                    }
                }, 100);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.event_around_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.evaround_add) {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of EventAroundFragment");
        fetchLocalEvent();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            fetchLocalEvent();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
