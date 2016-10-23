package me.hnguyenuml.spyday.UserContent;

/**
 * Created by Hung Nguyen on 10/23/2016.
 */

public class Message {

    public static final int TYPE_MESSAGE_FROM_ME = 0;
    public static final int TYPE_MESSAGE_FROM_FRIEND = 1;
    public static final int TYPE_MESSAGE_HEADER = 2;
    public static final int TYPE_MESSAGE_FOOTER = 3;
    public static final int TYPE_STICKER_FROM_ME = 4;
    public static final int TYPE_STICKER_FROM_FRIEND = 5;
    public static final int TYPE_MESSAGE_DATE = 6;

    private int messageType;
    private String messageText;
    private String stickerUrl;
    private boolean isChecked = false;
    private boolean isWarning = false;
    private boolean mIsShowSentStatus = false;
    private boolean mIsShowDate = false;

    public boolean getVisibilityStatus(){
        return this.mIsShowSentStatus;
    }

    public void setVisibilityStatus(boolean status){
        this.mIsShowSentStatus = status;
    }

    public boolean getVisibilityDate(){
        return this.mIsShowDate;
    }

    public void setVisibilityDate(boolean status){
        this.mIsShowDate = status;
    }


    public boolean isWarning(){
        return isWarning;
    }

    public void setIsWarning(boolean isWarning){
        this.isWarning = isWarning;
    }

    public boolean isMyself() {
        return messageType == TYPE_MESSAGE_FROM_ME || messageType == TYPE_STICKER_FROM_ME;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }

    public static boolean isMyself(int mViewType) {
        return mViewType == TYPE_STICKER_FROM_ME || mViewType == TYPE_MESSAGE_FROM_ME;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
