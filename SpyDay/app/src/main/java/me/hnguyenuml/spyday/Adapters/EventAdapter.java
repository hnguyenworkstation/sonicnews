package me.hnguyenuml.spyday.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UserContent.Event;
import me.hnguyenuml.spyday.UserContent.EventImageView;

/**
 * Created by jason on 11/4/16.
 */

public class EventAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Event> feedItems;
    ImageLoader imageLoader = SpyDayApplication.getInstance().getImageLoader();

    public EventAdapter(Activity activity, List<Event> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.event_withimage_item, null);

        if (imageLoader == null)
            imageLoader = SpyDayApplication.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.event_username);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.event_timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.event_statusmsg);
        TextView url = (TextView) convertView.findViewById(R.id.event_urlview);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.event_profileimg);
        EventImageView feedImageView = (EventImageView) convertView
                .findViewById(R.id.event_image);

        Event item = feedItems.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                .setResponseObserver(new EventImageView.ResponseObserver() {
                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onSuccess() {
                    }
                });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
