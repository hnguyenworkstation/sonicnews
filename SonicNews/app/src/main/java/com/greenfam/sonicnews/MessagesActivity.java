package com.greenfam.sonicnews;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnSend;
    private EditText inputMsg;

    // Chat messages list adapter
    private MessageListAdapter adapter;
    private List<SingleMessage> listMessages;
    private ListView listViewMessages;

    // Client name
    private String name = null;

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_chat);

        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");

        listMessages = new ArrayList<SingleMessage>();

        adapter = new MessageListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending message to web socket server
                String messContent = inputMsg.getText().toString();
                SingleMessage newMessage;

                // Todo: fix this testing function
                if (messContent.charAt(0) == '1') {
                    newMessage = new SingleMessage("you",
                            inputMsg.getText().toString(), false);
                } else {
                    newMessage = new SingleMessage("Other",
                            inputMsg.getText().toString(), true);
                }

                // Clearing the input filed once  message was sent
                appendMessage(newMessage);
                inputMsg.setText("");
            }
        });

        /**
         * Creating web socket client. This will have callback methods
         * */
    }


    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     * */
//    private void parseMessage(final String msg) {
//
//        try {
//            JSONObject jObj = new JSONObject(msg);
//
//            // JSON node 'flag'
//            String flag = jObj.getString("flag");
//
//            // if flag is 'self', this JSON contains session id
//            if (flag.equalsIgnoreCase(TAG_SELF)) {
//
//                String sessionId = jObj.getString("sessionId");
//
//                // Save the session id in shared preferences
//                utils.storeSessionId(sessionId);
//
//                Log.e(TAG, "Your session id: " + utils.getSessionId());
//
//            } else if (flag.equalsIgnoreCase(TAG_NEW)) {
//                // If the flag is 'new', new person joined the room
//                String name = jObj.getString("name");
//                String message = jObj.getString("message");
//
//                // number of people online
//                String onlineCount = jObj.getString("onlineCount");
//
//                showToast(name + message + ". Currently " + onlineCount
//                        + " people online!");
//
//            } else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
//                // if the flag is 'message', new message received
//                String fromName = name;
//                String message = jObj.getString("message");
//                String sessionId = jObj.getString("sessionId");
//                boolean isSelf = true;
//
//                // Checking if the message was sent by you
//                if (!sessionId.equals(utils.getSessionId())) {
//                    fromName = jObj.getString("name");
//                    isSelf = false;
//                }
//
//                Message m = new Message(fromName, message, isSelf);
//
//                // Appending the message to chat list
//                appendMessage(m);
//
//            } else if (flag.equalsIgnoreCase(TAG_EXIT)) {
//                // If the flag is 'exit', somebody left the conversation
//                String name = jObj.getString("name");
//                String message = jObj.getString("message");
//
//                showToast(name + message);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Appending message to list view
     * */
    private void appendMessage(final SingleMessage m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);

                adapter.notifyDataSetChanged();

                // Playing device's notification
                playBeep();
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Plays device's default notification sound
     * */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
