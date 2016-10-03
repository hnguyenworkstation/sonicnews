package me.hnguyenuml.spyballoon.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import me.hnguyenuml.spyballoon.R;
import me.hnguyenuml.spyballoon.UserContent.SBFriend;

/**
 * Created by jason on 10/3/16.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SBFriend> friendsList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phoneNum;
        public Button invite;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.sb_contact_name);
            phoneNum = (TextView) view.findViewById(R.id.sb_contact_phone);
            invite = (Button) view.findViewById(R.id.sb_invite);
        }
    }

    public ContactAdapter(Context mContext, ArrayList<SBFriend> friendsList) {
        this.mContext = mContext;
        this.friendsList = friendsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nonsb_contact_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SBFriend account = friendsList.get(position);
        holder.name.setText(account.getName());
        holder.phoneNum.setText(account.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ContactAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ContactAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    }
