package me.hnguyenuml.spyday.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.hnguyenuml.spyday.R;

/**
 * Created by Hung Nguyen on 10/20/2016.
 */

public class RecycleViewItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public RecycleViewItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.linear_separator);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
