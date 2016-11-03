package me.hnguyenuml.spyday;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.hnguyenuml.spyday.Adapters.MessageAdapter;
import me.hnguyenuml.spyday.Fragments.ListMessagesFragment;
import me.hnguyenuml.spyday.Fragments.StickerItemFragment;
import me.hnguyenuml.spyday.Fragments.StickerKeyboardFragment;
import me.hnguyenuml.spyday.Adapters.ChatRoomsAdapter;
import me.hnguyenuml.spyday.UserContent.Message;

public class ChatRoomActivity extends BaseInputActivity implements View.OnClickListener ,
        StickerKeyboardFragment.OnClickStickerListener,
        StickerItemFragment.OnStickerItemFragmentInteractionListener,
        ListMessagesFragment.OnFragmentInteractionListener,
        StickerKeyboardFragment.OnFragmentInteractionListener {

    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private String chatroomID;
    private String chatroomTitle;
    private EditText mMessageInput;
    private ImageButton mEmoBtn;
    private ImageButton mSendBtn;
    private InputMethodManager mInputManager;
    private TextView mIsTyping;
    private View emojiView;

    private boolean isShowingKeyboard = false;
    private boolean isShowingEmoji = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // get current intent
        Intent intent = getIntent();
        chatroomID = intent.getStringExtra("chat_room_id");
        chatroomTitle = intent.getStringExtra("name");

        if (chatroomID == null) {
            Toast.makeText(getApplicationContext(),
                    "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setting up the chat system
        initChatSystem();

        // Setting up chat UI
        initChatUI();

        transformSendBtn(mMessageInput);
        mMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                transformSendBtn(mMessageInput);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatroom_container, ListMessagesFragment.newInstance(TAG, chatroomID))
                    .commit();
        }
    }

    private void initChatSystem() {
        Toolbar chatToolbar = (Toolbar) findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(chatToolbar);
        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(chatroomTitle);
    }

    private void initChatUI() {
        emojiView = findViewById(R.id.chatroom_emokeyboard);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIsTyping = (TextView)findViewById(R.id.chatroom_istyping);
        mMessageInput = (EditText) findViewById(R.id.chatroom_inputtext);
        mEmoBtn = (ImageButton) findViewById(R.id.chatroom_emobtn);
        mEmoBtn.setOnClickListener(this);
        mEmoBtn.setSelected(true);

        mSendBtn = (ImageButton) findViewById(R.id.chatroom_sendbtn);
        mSendBtn.setOnClickListener(this);
    }

    private void transformSendBtn(EditText currentInput) {
        String currentMessage = currentInput.getText().toString();
        if (currentMessage.length() > 0) {
            mSendBtn.setEnabled(true);
            mIsTyping.setVisibility(View.VISIBLE);
        } else {
            mSendBtn.setEnabled(false);
            mIsTyping.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatroom_emobtn:
                emoButtonClicked(v);
                break;
            case R.id.chatroom_sendbtn:
                sendMessage(v);
                break;
            default:
                break;

        }
    }

    private void sendMessage(View view) {
        String msg;
        msg = mMessageInput.getText().toString();
        if (msg.isEmpty()) {
            return;
        }
        ListMessagesFragment mfragment = getMessageFragment();

        if (mfragment != null) {
            mfragment.sendPlainMessage(msg, chatroomID);
        }
        mMessageInput.setText("");
    }

    private ListMessagesFragment getMessageFragment() {
        ListMessagesFragment mfragment = (ListMessagesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.chatroom_container);
        mfragment.setRoomId(chatroomID);

        return mfragment;
    }

    public void emoButtonClicked(View view) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (isShowingKeyboard) {
            mInputManager.hideSoftInputFromWindow(
                    view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            isShowingEmoji = true;
        } else {
            if (mEmoView.getVisibility() == View.VISIBLE) {
                mInputManager.showSoftInput(
                        mMessageInput, 0);
            } else {
                showDefaultKeyboard(true);
            }
        }
    }

    private void showDefaultKeyboard(boolean isShowFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment emo_fragment = getSupportFragmentManager()
                .findFragmentById(R.id.chatroom_emokeyboard);
        ListMessagesFragment fm = getMessageFragment();

        if (emo_fragment != null) {
            ft.setCustomAnimations(R.anim.slide_in_from_bottom,
                    R.anim.fix_anim);
            if (isShowFragment) {
                mEmoView.setVisibility(View.VISIBLE);
                ft.show(emo_fragment);
                if (fm != null) {
                    fm.scrollToLast();
                }
                mEmoBtn.setSelected(false);
            } else {
                mEmoView.setVisibility(View.GONE);
                ft.hide(emo_fragment);
                mEmoBtn.setSelected(true);
            }
        } else {
            if (isShowFragment) {
                mEmoView.setVisibility(View.VISIBLE);
                ft.setCustomAnimations(R.anim.slide_in_from_bottom,
                        R.anim.slide_out_to_bottom);
                ft.add(R.id.chatroom_emokeyboard, new StickerKeyboardFragment());
                mEmoBtn.setSelected(false);
                if (fm != null)
                    fm.scrollToLast();
            }
        }
        ft.commit();
    }

    @Override
    public void onClickSticker(Uri uri) {
        boolean isTicker = true;
        boolean isMe = true;
        ListMessagesFragment fm = getMessageFragment();
        if (fm != null) {
            fm.sendMessage(isTicker, isMe);
        }
    }

    @Override
    public void onBackPressed() {
        if (mEmoView.getVisibility() == View.VISIBLE) {
            showDefaultKeyboard(false);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public View getEmoKeyboardView() {
        emojiView = findViewById(R.id.chatroom_emokeyboard);
        return emojiView;
    }

    @Override
    public void onShowEmojiKeyboard() {
        Log.v(TAG, "on Show Keyboard");
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showDefaultKeyboard(false);
        isShowingKeyboard = true;
        ListMessagesFragment fm = getMessageFragment();
        if (fm != null) {
            fm.scrollToLast();
        }
    }

    @Override
    public void onHideEmojiKeyboard() {
        Log.v(TAG, "On Hide Keyboard");
        isShowingKeyboard = false;
        if (isShowingEmoji) {
            showDefaultKeyboard(true);
            isShowingEmoji = false;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
