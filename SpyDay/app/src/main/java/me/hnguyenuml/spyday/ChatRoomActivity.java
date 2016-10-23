package me.hnguyenuml.spyday;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.hnguyenuml.spyday.Fragments.ListMessagesFragment;
import me.hnguyenuml.spyday.Fragments.StickerItemFragment;
import me.hnguyenuml.spyday.Fragments.StickerKeyboardFragment;

public class ChatRoomActivity extends BaseInputActivity implements View.OnClickListener ,
        StickerKeyboardFragment.OnClickStickerListener,
        StickerItemFragment.OnStickerItemFragmentInteractionListener,
        ListMessagesFragment.OnFragmentInteractionListener,
        StickerKeyboardFragment.OnFragmentInteractionListener {

    private static final String TAG = ChatRoomActivity.class.getSimpleName();
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

        emojiView = findViewById(R.id.chatroom_emokeyboard);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIsTyping = (TextView)findViewById(R.id.chatroom_istyping);
        mMessageInput = (EditText) findViewById(R.id.chatroom_inputtext);
        mEmoBtn = (ImageButton) findViewById(R.id.chatroom_emobtn);
        mEmoBtn.setOnClickListener(this);
        mEmoBtn.setSelected(true);

        mSendBtn = (ImageButton) findViewById(R.id.chatroom_sendbtn);
        transformSendBtn(mMessageInput);
        mSendBtn.setOnClickListener(this);

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
                    .add(R.id.container, ListMessagesFragment.newInstance(TAG, "Read Messages"))
                    .commit();
        }
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
                return;
            case R.id.chatroom_sendbtn:
                sendMessage(v);
                return;
            default:
                return;

        }
    }

    private void sendMessage(View view) {
        String msg;
        msg = mMessageInput.getText().toString();
        if (msg.isEmpty()) {
            return;
        }
        ListMessagesFragment mfragment = (ListMessagesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        if (mfragment != null) {
            mfragment.addMessage(msg);
        }
        mMessageInput.setText("");
    }

    public void emoButtonClicked(View view) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (isShowingKeyboard) {
            mInputManager.hideSoftInputFromWindow(
                    mMessageInput.getWindowToken(), 0);
            isShowingEmoji = true;
        } else {
            if (mEmoView.getVisibility() == View.VISIBLE) {
                mInputManager.showSoftInput(
                        mMessageInput, 0);
            } else {
                showFragment(true);
            }
        }
    }

    private void showFragment(boolean isShowFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment emo_fragment = getSupportFragmentManager().findFragmentById(R.id.chatroom_emokeyboard);
        if (emo_fragment != null) {
            ft.setCustomAnimations(R.anim.slide_in_from_bottom,
                    R.anim.slide_out_to_bottom);
            if (isShowFragment) {
                mEmoView.setVisibility(View.VISIBLE);
                ft.show(emo_fragment);
                ListMessagesFragment fm = (ListMessagesFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.container);
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
                ListMessagesFragment fm = (ListMessagesFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                if (fm != null) {
                    fm.scrollToLast();
                }
            }
        }
        ft.commit();
    }

    @Override
    public void onClickSticker(Uri uri) {
        boolean isTicker = true;
        boolean isMe = true;
        ListMessagesFragment fm = (ListMessagesFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (fm != null) {
            fm.sendMessage(isTicker, isMe);
        }
    }

    @Override
    public void onBackPressed() {
        if (mEmoView.getVisibility() == View.VISIBLE) {
            showFragment(false);
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
        showFragment(false);
        isShowingKeyboard = true;
        ListMessagesFragment fm = (ListMessagesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        if (fm != null) {
            fm.scrollToLast();
        }
    }

    @Override
    public void onHideEmojiKeyboard() {
        Log.v(TAG, "On Hide Keyboard");
        isShowingKeyboard = false;
        if (isShowingEmoji) {
            showFragment(true);
            isShowingEmoji = false;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
