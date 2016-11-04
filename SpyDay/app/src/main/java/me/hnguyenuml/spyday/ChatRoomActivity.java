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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.Fragments.InputOptionsFragment;
import me.hnguyenuml.spyday.Fragments.ListMessagesFragment;
import me.hnguyenuml.spyday.Fragments.StickerItemFragment;
import me.hnguyenuml.spyday.Fragments.StickerKeyboardFragment;
import me.hnguyenuml.spyday.UI.CustomPopupWindow;
import me.hnguyenuml.spyday.UserContent.MapContent;
import me.hnguyenuml.spyday.UserContent.Message;

public class ChatRoomActivity extends BaseInputActivity implements View.OnClickListener ,
        StickerKeyboardFragment.OnClickStickerListener,
        StickerItemFragment.OnStickerItemFragmentInteractionListener,
        ListMessagesFragment.OnFragmentInteractionListener,
        StickerKeyboardFragment.OnFragmentInteractionListener,
        InputOptionsFragment.OnFragmentInteractionListener {

    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    private String chatroomID;
    private String chatroomTitle;
    private EditText mMessageInput;

    private ImageButton mEmoBtn;
    private ImageButton mSendBtn;
    private ImageButton mCameraBtn;
    private ImageButton mGalleryBtn;
    private ImageButton mAttachBtn;
    private ImageButton mIconBtn;
    private ImageButton mLocationBtn;
    private ImageButton mRecordBtn;

    private InputMethodManager mInputManager;
    private TextView mIsTyping;
    private View keyboardViewer;
    private LayoutInflater layoutInflater;
    private RelativeLayout mRootLayout;

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int LOCATION_REQUEST = 3;
    private static final int RECORDVOID_REQUEST = 4;

    private boolean isShowingKeyboard = false;
    private boolean isShowingEmoji = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mRootLayout = (RelativeLayout) findViewById(R.id.activity_chat_room);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatroom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.crmenu_phonecall:
                Toast.makeText(this, "Phone Call", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.crmenu_videocall:
                Toast.makeText(this, "Video Call", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.crmenu_more:
                Toast.makeText(this, "More", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initChatUI() {
        keyboardViewer = findViewById(R.id.chatroom_keyboardholder);
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIsTyping = (TextView)findViewById(R.id.chatroom_istyping);
        mMessageInput = (EditText) findViewById(R.id.chatroom_inputtext);

        mEmoBtn = (ImageButton) findViewById(R.id.chatroom_emobtn);
        mEmoBtn.setOnClickListener(this);
        mEmoBtn.setSelected(true);

        mSendBtn = (ImageButton) findViewById(R.id.chatroom_sendbtn);
        mSendBtn.setOnClickListener(this);

        mCameraBtn = (ImageButton) findViewById(R.id.chatroom_opencamera);
        mCameraBtn.setOnClickListener(this);

        mGalleryBtn = (ImageButton) findViewById(R.id.chatroom_opengallery);
        mGalleryBtn.setOnClickListener(this);

        mAttachBtn = (ImageButton) findViewById(R.id.chatroom_attachfile);
        mAttachBtn.setOnClickListener(this);

        mIconBtn = (ImageButton) findViewById(R.id.chatroom_openiconkeyboard);
        mIconBtn.setOnClickListener(this);

        mLocationBtn = (ImageButton) findViewById(R.id.chatroom_sendlocation);
        mLocationBtn.setOnClickListener(this);

        mRecordBtn = (ImageButton) findViewById(R.id.chatroom_sendvoicerecord);
        mRecordBtn.setOnClickListener(this);
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
            case R.id.chatroom_opencamera:
                break;
            case R.id.chatroom_opengallery:
                break;
            case R.id.chatroom_attachfile:
                break;
            case R.id.chatroom_openiconkeyboard:
                break;
            case R.id.chatroom_sendlocation:
                openLocationIntent();
                break;
            case R.id.chatroom_sendvoicerecord:
                break;
            default:
                break;

        }
    }

    private void openLocationIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), LOCATION_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void showPopupOption() {
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.option_popup_window, null);

        final CustomPopupWindow pWindow = new CustomPopupWindow(container, 400, 400, true);
        pWindow.showAtLocation(mRootLayout, Gravity.NO_GRAVITY, 0, 550);

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pWindow.dismiss();
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    MapContent mapModel = new MapContent(String.valueOf(latLng.latitude)
                            , String.valueOf(latLng.longitude));
                    Message message = new Message(chatroomID,
                            SpyDayApplication.getInstance().getPrefManager().getCurrentUID(),
                            mapModel,
                            String.valueOf(Calendar.getInstance().getTime().getTime()),
                            Message.MODEL_LOCATION_MESSAGE);
                    getMessageFragment().postMessageToServer(message);
                } else {
                    //PLACE IS NULL
                }
            }
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

    private void emoButtonClicked(View view) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (isShowingKeyboard) {
            mInputManager.hideSoftInputFromWindow(
                    view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            isShowingEmoji = true;
        } else {
            if (this.keyboardViewer.getVisibility() == View.VISIBLE) {
                mInputManager.showSoftInput(
                        mMessageInput, 0);
            } else {
                showEmoKeyboard(true);
            }
        }
    }

    private void showEmoKeyboard(boolean isShowFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment emo_fragment = getSupportFragmentManager()
                .findFragmentById(R.id.chatroom_keyboardholder);
        ListMessagesFragment fm = getMessageFragment();

        if (emo_fragment != null) {
            ft.setCustomAnimations(R.anim.slide_in_from_bottom,
                    R.anim.fix_anim);
            if (isShowFragment) {
                this.keyboardViewer.setVisibility(View.VISIBLE);
                ft.show(emo_fragment);
                if (fm != null) {
                    fm.scrollToLast();
                }
                mEmoBtn.setSelected(false);
            } else {
                this.keyboardViewer.setVisibility(View.GONE);
                ft.hide(emo_fragment);
                mEmoBtn.setSelected(true);
            }
        } else {
            if (isShowFragment) {
                this.keyboardViewer.setVisibility(View.VISIBLE);
                ft.setCustomAnimations(R.anim.slide_in_from_bottom,
                        R.anim.slide_out_to_bottom);
                ft.add(R.id.chatroom_keyboardholder, new StickerKeyboardFragment());
                mEmoBtn.setSelected(false);
                if (fm != null)
                    fm.scrollToLast();
            }
        }
        ft.commitAllowingStateLoss();
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
        if (this.keyboardViewer.getVisibility() == View.VISIBLE) {
            showEmoKeyboard(false);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public View getEmoKeyboardView() {
        keyboardViewer = findViewById(R.id.chatroom_keyboardholder);
        return keyboardViewer;
    }

    @Override
    public void onShowEmojiKeyboard() {
        Log.v(TAG, "on Show Keyboard");
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showEmoKeyboard(false);
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
            showEmoKeyboard(true);
            isShowingEmoji = false;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}
