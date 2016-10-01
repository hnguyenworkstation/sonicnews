package com.greenfam.sonicnews.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.greenfam.sonicnews.Adapters.ConversationAdapter;
import com.greenfam.sonicnews.FCM.NotificationUtils;
import com.greenfam.sonicnews.SonicNewsActivity;
import com.greenfam.sonicnews.Content.AppConfig;
import com.greenfam.sonicnews.Content.MyPreferenceManager;
import com.greenfam.sonicnews.Content.ServerEndpoint;
import com.greenfam.sonicnews.Content.SingleConversation;
import com.greenfam.sonicnews.Content.SingleMessageDivider;
import com.greenfam.sonicnews.GCM.GcmIntentService;
import com.greenfam.sonicnews.LoginActivity;
import com.greenfam.sonicnews.MessagesActivity;
import com.greenfam.sonicnews.R;
import com.greenfam.sonicnews.SingleMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button chatButton;

    private OnFragmentInteractionListener mListener;

    public static final String TAG = MessageFragment.class
            .getSimpleName();
    private RequestQueue mRequestQueue;
    private static MessageFragment mInstance;
    private MyPreferenceManager pref;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<SingleConversation> chatRoomArrayList;
    private ConversationAdapter mAdapter;
    private RecyclerView recyclerView;


    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.available_chat_list);

        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    subscribeToGlobalTopic();
                } else if (intent.getAction().equals(AppConfig.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.e(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ConversationAdapter(this.getContext(), chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SingleMessageDivider(
                this.getContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ConversationAdapter.RecyclerTouchListener(
                this.getContext(), recyclerView, new ConversationAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                SingleConversation chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(getActivity(), MessagesActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /**
         * Always check for google play services availability before
         * proceeding further with GCM
         * */
        if (checkPlayServices()) {
            registerGCM();
            fetchChatRooms();
        }

        return rootView;
    }

    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == AppConfig.PUSH_TYPE_CHATROOM) {
            SingleMessage message = (SingleMessage) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");

            if (message != null && chatRoomId != null) {
                updateRow(chatRoomId, message);
            }
        } else if (type == AppConfig.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            SingleMessage message = (SingleMessage) intent.getSerializableExtra("message");
            Toast.makeText(this.getContext(), "New push: " + message.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the chat list unread count and the last message
     */
    private void updateRow(String chatRoomId, SingleMessage message) {
        for (SingleConversation cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * fetching the chat rooms by making http call
     */
    private void fetchChatRooms() {
        StringRequest strReq = new StringRequest(Request.Method.GET,
            ServerEndpoint.CHAT_ROOMS, new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            Log.e(TAG, "response: " + response);

            try {
                JSONObject obj = new JSONObject(response);

                // check for error flag
                if (obj.getBoolean("error") == false) {
                    JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");
                    for (int i = 0; i < chatRoomsArray.length(); i++) {
                        JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                        SingleConversation cr = new SingleConversation();
                        cr.setId(chatRoomsObj.getString("chat_room_id"));
                        cr.setName(chatRoomsObj.getString("name"));
                        cr.setLastMessage("");
                        cr.setUnreadCount(0);
                        cr.setTimestamp(chatRoomsObj.getString("created_at"));
                        chatRoomArrayList.add(cr);
                    }
                } else {
                    // error in fetching chat rooms
                    Toast.makeText(getContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            mAdapter.notifyDataSetChanged();

            // subscribing to all chat room topics
            subscribeToAllTopics();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        SonicNewsActivity.getInstance().addToRequestQueue(strReq);
    }

    // subscribing to global topic
    private void subscribeToGlobalTopic() {
        Intent intent = new Intent(this.getContext(), GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
        intent.putExtra(GcmIntentService.TOPIC, AppConfig.TOPIC_GLOBAL);
        this.getActivity().startService(intent);
    }

    // Subscribing to all chat room topics
    // each topic name starts with `topic_` followed by the ID of the chat room
    // Ex: topic_1, topic_2
    private void subscribeToAllTopics() {
        for (SingleConversation cr : chatRoomArrayList) {

            Intent intent = new Intent(this.getContext(), GcmIntentService.class);
            intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
            intent.putExtra(GcmIntentService.TOPIC, "topic_" + cr.getId());
            getActivity().startService(intent);
        }
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications(getContext());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra("key", "register");
        getActivity().startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
