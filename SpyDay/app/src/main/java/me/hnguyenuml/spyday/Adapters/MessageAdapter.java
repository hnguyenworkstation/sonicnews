package me.hnguyenuml.spyday.Adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.List;

import me.hnguyenuml.spyday.BasicApp.SpyDayApplication;
import me.hnguyenuml.spyday.R;
import me.hnguyenuml.spyday.UserContent.Message;
import me.hnguyenuml.spyday.Utils.MapUtils;

/**
 * Created by Hung Nguyen on 10/23/2016.
 * Implemented based on FrankNT's MessageAdapter
 */

public class MessageAdapter extends FirebaseRecyclerAdapter< Message ,MessageAdapter.ViewHolder >{

    private List<Message> mDataset;
    private boolean isDeleteMsg = false;
    private boolean isSelectAll = false;
    private int mFirstVisiblePosition;
    private static OnClickChatScreenListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final int mViewType;
        private final ImageView mCheck;
        private ImageView mImgWarning;

        private TextView mTextView;
        private ImageView mAvatar;
        private ImageView mImageView;
        private ImageView mImageViewStatus;
        private RelativeLayout mChatStatusView;
        private ImageView mImageContainer;

        private Context mContext;

        private ViewHolder(ViewGroup v, int viewType) {
            super(v);
            mContext = v.getContext();

            this.mTextView = (TextView) v.findViewById(R.id.message_content);
            this.mAvatar = (ImageView) v.findViewById(R.id.message_avatar);
            this.mImageView = (ImageView) v.findViewById(R.id.sticker_image);
            this.mImgWarning = (ImageView) v.findViewById(R.id.message_warningicon);
            this.mChatStatusView = (RelativeLayout) v.findViewById(R.id.message_chatstatusview);
            this.mImageViewStatus = (ImageView) v.findViewById(R.id.message_status_image);
            this.mImageContainer = (ImageView) v.findViewById(R.id.message_imagecontainer);

            if (this.mImgWarning != null) {
                this.mImgWarning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onErrorMessageClick(v);
                    }
                });
            }

            this.mViewType = viewType;
            this.mCheck = (ImageView) v.findViewById(R.id.message_checkbox);
            if (mCheck != null) {
                mCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCheck.setSelected(!mCheck.isSelected());
                    }
                });
            }

            if (this.mTextView != null) {
                if (mListener != null) {
                    this.mTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onMessageClick(v, getAdapterPosition() - 1);
                        }
                    });
                }
                this.mTextView.setOnLongClickListener(this);

                if (Message.isMyself(this.mViewType)) {

                } else {

                }
            }

            if (this.mImageView != null) {
                if (Message.isMyself(this.mViewType)) {

                } else {

                }
                this.mImageView.setOnLongClickListener(this);
                if (mListener != null) {
                    this.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onStickerMessageClick(v, getAdapterPosition() - 1);
                        }
                    });
                }
            }

            if (this.mImageContainer != null) {
                if (Message.isMyself(this.mViewType)) {

                } else {

                }
                this.mImageContainer.setOnLongClickListener(this);
                if (mListener != null) {
                    this.mImageContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onStickerMessageClick(v, getAdapterPosition() - 1);
                        }
                    });
                }
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }

        public void setImageFromURL(String url) {
            if (mImageContainer == null) return;
            Glide.with(mImageContainer.getContext()).load(url)
                    .override(400, 2500)
                    .fitCenter()
                    .into(mImageContainer);
            mImageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    public MessageAdapter(List<Message> myDataset) {
        super(Message.class, R.layout.message_from_friend, MessageAdapter.ViewHolder.class,
                SpyDayApplication.getInstance().getPrefManager().getFirebaseDatabase());
        mDataset = myDataset;
    }

    public void setOnChatScreenClickListener(OnClickChatScreenListener listener) {
        mListener = listener;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case Message.TYPE_MESSAGE_FROM_ME:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_from_me, parent, false);
                break;
            case Message.TYPE_MESSAGE_FROM_FRIEND:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_from_friend, parent, false);
                break;
            case Message.TYPE_MESSAGE_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_header, parent, false);
                break;
            case Message.TYPE_MESSAGE_FOOTER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_footer, parent, false);
                break;
            case Message.TYPE_STICKER_FROM_ME:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sticker_from_me, parent, false);
                break;
            case Message.TYPE_STICKER_FROM_FRIEND:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sticker_from_friend, parent, false);
                break;
            case Message.TYPE_MESSAGE_DATE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_date, parent, false);
                break;
            case Message.TYPE_MESSAGE_IMAGE_FROM_ME:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_withimage_from_me, parent, false);
                break;
            default:
                break;
        }

        ViewHolder vh = new ViewHolder((ViewGroup) v, viewType);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Message.TYPE_MESSAGE_HEADER;
        } else if (position == mDataset.size() + 1) {
            return Message.TYPE_MESSAGE_FOOTER;
        } else {
            return mDataset.get(position - 1).getMessageType();
        }
    }

    public int getCurrentPosition() {
        return mFirstVisiblePosition;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.v("test", "pos: " + (position - 1));
        if (position == 0) {
            // header
        } else if (position == mDataset.size() + 1) {
            // Footer
        } else if (mDataset.get(position - 1).getMessageType() == Message.TYPE_MESSAGE_DATE) {

        } else {
            if (mDataset.get(position - 1).isMyself()) {
                View bg;
                if (position > 15) {
                } else {
                }
                boolean isSticker = false;
                switch (mDataset.get(position - 1).getMessageType()) {
                    case Message.TYPE_STICKER_FROM_ME:
                        bg = holder.mImageView;
                        isSticker = true;
                        break;
                    case Message.TYPE_MESSAGE_FROM_ME:
                        bg = holder.mTextView;
                        holder.mTextView.setText(mDataset.get(position - 1).getMessageText());

                        if (mDataset.get(position - 1).isWarning())
                            holder.mTextView.setTextColor(holder.mContext.getResources().getColor(R.color.chat_warning_color));
                        else
                            holder.mTextView.setTextColor(holder.mContext.getResources().getColor(R.color.black));
                        break;
                    case Message.TYPE_MESSAGE_IMAGE_FROM_ME:
                        bg = holder.mImageContainer;
                        holder.setImageFromURL(MapUtils.getLocation(
                                mDataset.get(position - 1).getMapContent().getLatitude(),
                                mDataset.get(position - 1).getMapContent().getLongitude()));
                        break;
                    default:
                        bg = holder.mTextView;
                        holder.mTextView.setText(mDataset.get(position - 1).getMessageText());
                        break;
                }


                if (!isSticker && (mDataset.get(position - 1).getMapContent() == null) ) {
                    if (position - 1 > 0) {
                        if (mDataset.get(position - 2).isMyself()) {
                            if (position < mDataset.size()) {
                                if (mDataset.get(position).isMyself()) {
                                    bg.setBackgroundResource(R.drawable.bubble_white);
                                } else {
                                    bg.setBackgroundResource(R.drawable.bubble_right_white);
                                }
                            } else {
                                bg.setBackgroundResource(R.drawable.bubble_right_white);
                            }
                        } else {
                            bg.setBackgroundResource(R.drawable.bubble_left_white);
                        }
                    } else {
                        bg.setBackgroundResource(R.drawable.bubble_left_white);
                    }

                    int padding = holder.mContext.getResources().getDimensionPixelOffset(R.dimen.margin_12);
                    bg.setPadding(padding, padding, padding, padding);
                }

                if (mDataset.get(position - 1).isWarning())
                    holder.mImgWarning.setVisibility(View.VISIBLE);
                else
                    holder.mImgWarning.setVisibility(View.INVISIBLE);
                boolean isVisibleDateHistory = mDataset.get(position - 1).getVisibilityDate();
                boolean isVisibleStatus = mDataset.get(position - 1).getVisibilityStatus();
                if (isVisibleStatus) {
                    holder.mChatStatusView.setVisibility(View.VISIBLE);
                    holder.mImageViewStatus.setVisibility(View.VISIBLE);
                    holder.mImageViewStatus.setVisibility(View.GONE);
                } else if (isVisibleDateHistory) {
                    holder.mChatStatusView.setVisibility(View.VISIBLE);
                    holder.mImageViewStatus.setVisibility(View.GONE);
                } else {
                    holder.mChatStatusView.setVisibility(View.GONE);
                }
            } else {
                View bg = null;
                boolean isSticker = false;
                switch (mDataset.get(position - 1).getMessageType()) {
                    case Message.TYPE_STICKER_FROM_FRIEND:
                        bg = holder.mImageView;
                        isSticker = true;
                        break;
                    case Message.TYPE_MESSAGE_FROM_FRIEND:
                        bg = holder.mTextView;
                        holder.mTextView.setText(mDataset.get(position - 1).getMessageText());
                        break;
                    default:
                        break;
                }

                if (mDataset.get(position - 1).getMapContent() == null) {
                    if (position - 1 > 0) {
                        if (!mDataset.get(position - 2).isMyself()) {
                            if (position < mDataset.size()) {
                                if (!mDataset.get(position).isMyself()) {
                                    setBackgroundResource(bg, isSticker, R.drawable.bubble_brown);
                                    holder.mAvatar.setVisibility(View.INVISIBLE);
                                } else {
                                    setBackgroundResource(bg, isSticker, R.drawable.bubble_right_brown);
                                    holder.mAvatar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                setBackgroundResource(bg, isSticker, R.drawable.bubble_right_brown);
                                holder.mAvatar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            setBackgroundResource(bg, isSticker, R.drawable.bubble_left_brown);
                            holder.mAvatar.setVisibility(View.VISIBLE);
                        }
                    } else {
                        setBackgroundResource(bg, isSticker, R.drawable.bubble_left_brown);
                        holder.mAvatar.setVisibility(View.INVISIBLE);
                    }
                }

                boolean isVisibleDateHistory = mDataset.get(position - 1).getVisibilityDate();
                if (isVisibleDateHistory) {
                    holder.mChatStatusView.setVisibility(View.VISIBLE);
                } else {
                    holder.mChatStatusView.setVisibility(View.GONE);
                }
            }

            holder.mCheck.setSelected(isSelectAll);
            if (isDeleteMsg) {
                holder.mCheck.setVisibility(View.VISIBLE);
            } else {
                holder.mCheck.setVisibility(View.GONE);
            }


        }
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, Message model, int position) {
        // Todo: Setting up view of each message
    }

    private void setBackgroundResource(View v, boolean isSticker, int idResource) {
        if (!isSticker)
            v.setBackgroundResource(idResource);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size() + 2;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public interface OnClickChatScreenListener {
        void onErrorMessageClick(View view);

        void onMessageClick(View view, int position);

        void onStickerMessageClick(View view, int position);

        void onMessageImageClick(View view, int position);
    }
}
