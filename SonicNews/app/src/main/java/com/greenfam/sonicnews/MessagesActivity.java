package com.greenfam.sonicnews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.greenfam.sonicnews.Adapters.MessageThreadAdapter;
import com.greenfam.sonicnews.Content.AppConfig;
import com.greenfam.sonicnews.FCM.NotificationUtils;
import com.greenfam.sonicnews.Content.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends SonicNewsActivity {

    private String TAG = MessagesActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView listMessages;
    private MessageThreadAdapter mAdapter;
    private ArrayList<SingleMessage> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        inputMessage = (EditText) findViewById(R.id.inputMsg);
        btnSend = (ImageButton) findViewById(R.id.btnSend);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        listMessages = (RecyclerView) findViewById(R.id.listMessages);
        messageArrayList = new ArrayList<>();

        // self user id is to identify the message owner
        String selfUserId = SonicNewsActivity.getInstance().getPrefManager().getUser().getId();
        mAdapter = new MessageThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listMessages.setLayoutManager(layoutManager);
        listMessages.setItemAnimator(new DefaultItemAnimator());
        listMessages.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchChatThread();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications(getBaseContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        SingleMessage message = (SingleMessage) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                listMessages.getLayoutManager().smoothScrollToPosition(listMessages, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String endPoint = ServerEndpoint.CHAT_ROOM_MESSAGE.replace("_ID_", chatRoomId);

        Log.e(TAG, "endpoint: " + endPoint);

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONObject commentObj = obj.getJSONObject("message");

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = obj.getJSONObject("user");
                        String userId = userObj.getString("user_id");

                        SingleMessage message = new SingleMessage();
                        message.setId(commentId);
                        message.setMessage(commentText);
                        message.setCreatedAt(createdAt);
                        message.setFromID(userId);

                        messageArrayList.add(message);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            listMessages.getLayoutManager().smoothScrollToPosition(listMessages, null, mAdapter.getItemCount() - 1);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                inputMessage.setText(message);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", SonicNewsActivity.getInstance().getPrefManager().getUser().getId());
                params.put("message", message);
                Log.e(TAG, "Params: " + params.toString());
                return params;
            };
        };

        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        SonicNewsActivity.getInstance().addToRequestQueue(strReq);
    }


    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {

        String endPoint = ServerEndpoint.CHAT_THREAD.replace("_ID_", chatRoomId);
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
            endPoint, new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            Log.e(TAG, "response: " + response);

            try {
                JSONObject obj = new JSONObject(response);
                // check for error
                if (obj.getBoolean("error") == false) {
                    JSONArray commentsObj = obj.getJSONArray("messages");

                    for (int i = 0; i < commentsObj.length(); i++) {
                        JSONObject commentObj = (JSONObject) commentsObj.get(i);

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = commentObj.getJSONObject("user");
                        String userId = userObj.getString("user_id");

                        SingleMessage message = new SingleMessage();
                        message.setId(commentId);
                        message.setMessage(commentText);
                        message.setCreatedAt(createdAt);
                        message.setFromID(userId);

                        messageArrayList.add(message);
                    }

                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        listMessages.getLayoutManager().smoothScrollToPosition(listMessages, null, mAdapter.getItemCount() - 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adding request to request queue
        SonicNewsActivity.getInstance().addToRequestQueue(strReq);
    }
}
