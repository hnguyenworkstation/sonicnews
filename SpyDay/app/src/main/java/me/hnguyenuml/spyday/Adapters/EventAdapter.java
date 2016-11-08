package me.hnguyenuml.spyday.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
import me.hnguyenuml.spyday.UserContent.ChatRoom;
import me.hnguyenuml.spyday.UserContent.Event;
import me.hnguyenuml.spyday.UserContent.EventImageView;
import me.hnguyenuml.spyday.UserContent.Message;

/**
 * Created by jason on 11/4/16.
 */

public class EventAdapter extends FirebaseRecyclerAdapter<Event, EventAdapter.ViewHolder > {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Event> feedItems;
    private final SpyDayApplication mInstance = SpyDayApplication.getInstance();
    private ImageLoader imageLoader = SpyDayApplication.getInstance().getImageLoader();

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUsername;
        private TextView mTimeStamp;
        private TextView mStatusMsg;
        private TextView mURL;
        private NetworkImageView mProfilePic;
        private EventImageView mEventImageView;
        private final int mViewType;
        private Context mContext;
        private ImageButton mLikeBtn;
        private ImageButton mCommentBtn;

        ViewHolder(ViewGroup v, int viewType) {
            super(v);
            mContext = v.getContext();
            this.mViewType = viewType;
            this.mUsername = (TextView) v.findViewById(R.id.event_username);
            this.mTimeStamp = (TextView) v.findViewById(R.id.event_timestamp);
            this.mStatusMsg = (TextView) v.findViewById(R.id.event_statusmsg);
            this.mURL = (TextView) v.findViewById(R.id.event_urlview);
            this.mProfilePic = (NetworkImageView) v.findViewById(R.id.event_profileimg);
            this.mEventImageView = (EventImageView) v.findViewById(R.id.event_image);
            this.mLikeBtn = (ImageButton) v.findViewById(R.id.event_likebtn);
            this.mCommentBtn = (ImageButton) v.findViewById(R.id.event_commentbtn);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        return feedItems.get(position).getType();
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case Event.TYPE_IMAGE_EVENT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_withimage_item, parent, false);
                break;
            default:
                break;
        }

        EventAdapter.ViewHolder vh = new EventAdapter.ViewHolder((ViewGroup) v, viewType);
        return vh;
    }

    public EventAdapter(Activity activity, List<Event> feedItems) {
        super(Event.class, R.layout.event_withimage_item, EventAdapter.ViewHolder.class,
               SpyDayApplication.getInstance().getPrefManager().getFirebaseDatabase());
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Event event, int position) {

    }

    @Override
    public void onBindViewHolder(final EventAdapter.ViewHolder viewHolder, int position) {
        Log.v("test", "pos: " + (position));

        Event tempEv = feedItems.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (imageLoader == null)
            imageLoader = SpyDayApplication.getInstance().getImageLoader();

        viewHolder.mUsername.setText(tempEv.getName());
        viewHolder.mTimeStamp.setText(ChatRoomsAdapter.getTimeStamp(tempEv.getTimeStamp()));

        // Chcek for empty status message
        if (!TextUtils.isEmpty(tempEv.getStatus())) {
            viewHolder.mStatusMsg.setText(tempEv.getStatus());
            viewHolder.mStatusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            viewHolder.mStatusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (tempEv.getUrl() != null) {
            viewHolder.mURL.setText(Html.fromHtml("<a href=\"" + tempEv.getUrl() + "\">"
                    + tempEv.getUrl() + "</a> "));

            // Making url clickable
            viewHolder.mURL.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.mURL.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            viewHolder.mURL.setVisibility(View.GONE);
        }

        // user profile pic
        viewHolder.mProfilePic.setImageUrl(tempEv.getProfilePic(), imageLoader);

        // Feed image
        if (tempEv.getImge() != null) {
            viewHolder.mEventImageView.setImageUrl(tempEv.getImge(), imageLoader);
            viewHolder.mEventImageView.setVisibility(View.VISIBLE);
            viewHolder.mEventImageView
                    .setResponseObserver(new EventImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            viewHolder.mEventImageView.setVisibility(View.GONE);
        }
    }

    public interface OnEventClickListener {}

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

}
