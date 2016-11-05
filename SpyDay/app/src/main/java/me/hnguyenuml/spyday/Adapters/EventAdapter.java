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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.Static.Endpoint;
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mUsername;
        TextView mTimeStamp;
        TextView mStatusMsg;
        TextView mURL;
        NetworkImageView mProfilePic;
        EventImageView mEventImageView;
        Context mContext;

        ViewHolder(View v) {
            super(v);
            mContext = v.getContext();
            this.mUsername = (TextView) v.findViewById(R.id.event_username);
            this.mTimeStamp = (TextView) v.findViewById(R.id.event_timestamp);
            this.mStatusMsg = (TextView) v.findViewById(R.id.event_statusmsg);
            this.mURL = (TextView) v.findViewById(R.id.event_urlview);
                this.mProfilePic = (NetworkImageView) v.findViewById(R.id.event_profileimg);
            this.mEventImageView = (EventImageView) v.findViewById(R.id.event_image);
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
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_withimage_item, parent, false);
                break;
        }

        EventAdapter.ViewHolder vh = new EventAdapter.ViewHolder(v);
        return vh;
    }

    public EventAdapter(Activity activity, List<Event> feedItems) {
        super(Event.class, R.layout.event_withimage_item, EventAdapter.ViewHolder.class,
               SpyDayApplication.getInstance().getPrefManager().getFirebaseDatabase());
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Event model, int position) {
        Log.v("test", "pos: " + (position));
        if (position == feedItems.size()) return;

        model = feedItems.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (imageLoader == null)
            imageLoader = SpyDayApplication.getInstance().getImageLoader();

        viewHolder.mUsername.setText(model.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(model.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        viewHolder.mTimeStamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(model.getStatus())) {
            viewHolder.mStatusMsg.setText(model.getStatus());
            viewHolder.mStatusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            viewHolder.mStatusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (model.getUrl() != null) {
            viewHolder.mURL.setText(Html.fromHtml("<a href=\"" + model.getUrl() + "\">"
                    + model.getUrl() + "</a> "));

            // Making url clickable
            viewHolder.mURL.setMovementMethod(LinkMovementMethod.getInstance());
            viewHolder.mURL.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            viewHolder.mURL.setVisibility(View.GONE);
        }

        // user profile pic
        viewHolder.mProfilePic.setImageUrl(model.getProfilePic(), imageLoader);

        // Feed image
        if (model.getImge() != null) {
            viewHolder.mEventImageView.setImageUrl(model.getImge(), imageLoader);
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
