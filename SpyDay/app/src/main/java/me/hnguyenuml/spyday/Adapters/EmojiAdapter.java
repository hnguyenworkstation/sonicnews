package me.hnguyenuml.spyday.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import me.hnguyenuml.spyday.R;

/**
 * Created by Hung Nguyen on 10/23/2016.
 */

public class EmojiAdapter extends BaseAdapter {
    Context context;

    LayoutInflater inflater;

    public EmojiAdapter(Context context, int resourceId) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    R.layout.fragment_sticker_item, parent, false);

            viewHolder.imgSticker = (ImageView) convertView
                    .findViewById(R.id.imgSticker);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private static class ViewHolder {
        ImageView imgSticker;
    }
}
