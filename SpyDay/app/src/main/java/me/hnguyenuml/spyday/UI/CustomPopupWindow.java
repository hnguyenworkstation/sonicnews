package me.hnguyenuml.spyday.UI;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by jason on 11/3/16.
 */

public class CustomPopupWindow extends PopupWindow {

    public CustomPopupWindow(Context context) {
        super(context);
    }


    public CustomPopupWindow(View contentView, int width, int height, boolean focusable){
        super(contentView, width, height, focusable);
    }

}
