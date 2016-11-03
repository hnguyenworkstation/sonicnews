package me.hnguyenuml.spyday;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import me.hnguyenuml.spyday.BasicApp.HardwareUtils;

public abstract class BaseInputActivity extends AppCompatActivity {
    public static final String TAG = "SPYDAY";
    protected RelativeLayout keyboardViewer;
    public static final String KEYBOARD_HEIGHT = "keyboard_height";

    @Override
    protected void onStart() {
        super.onStart();
        keyboardViewer = (RelativeLayout) getEmoKeyboardView();

        final ViewGroup groupViewLayout = (ViewGroup) findViewById(android.R.id.content);
        int savedHeight = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(KEYBOARD_HEIGHT,
                        (int) getResources().getDimension(
                                R.dimen.keyboard_height_default));
        setEmoKeyboardHeight(keyboardViewer, savedHeight);

        groupViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private int AlphaKeyboardHeight;
                    private boolean hasCallback;

                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        groupViewLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight;
                        if (Build.VERSION.SDK_INT >= 5.0) {
                            screenHeight = calculateScreenHeightForLollipop();
                        } else {
                            screenHeight = groupViewLayout.getRootView().getHeight();
                        }
                        int keyboardHeight = screenHeight - (r.bottom);
                        if (HardwareUtils.hasNavBar(BaseInputActivity.this)) {
                            int resourceId = getResources()
                                    .getIdentifier("navigation_bar_height",
                                            "dimen", "android");
                            if (resourceId > 0) {
                                keyboardHeight -= getResources()
                                        .getDimensionPixelSize(resourceId);
                            }
                        }

                        if (keyboardHeight > 100) {
                            if (AlphaKeyboardHeight != keyboardHeight) {
                                AlphaKeyboardHeight = keyboardHeight;
                                setEmoKeyboardHeight(keyboardViewer, AlphaKeyboardHeight);

                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(BaseInputActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt(KEYBOARD_HEIGHT, AlphaKeyboardHeight);
                                Log.v(TAG, "oldKeyboardHeight: " + AlphaKeyboardHeight);
                                editor.apply();
                            }
                            if (!hasCallback) {
                                hasCallback = true;
                                onShowEmojiKeyboard();
                            }
                        } else {
                            hasCallback = false;
                            onHideEmojiKeyboard();
                        }
                    }
                });
    }

    public abstract View getEmoKeyboardView();
    public abstract void onShowEmojiKeyboard();
    public abstract void onHideEmojiKeyboard();

    private void setEmoKeyboardHeight(RelativeLayout stickerSet, int savedHeight) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, savedHeight);
        stickerSet.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public int calculateScreenHeightForLollipop() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
