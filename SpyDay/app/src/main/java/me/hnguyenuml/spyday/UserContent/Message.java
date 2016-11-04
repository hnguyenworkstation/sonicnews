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
    public static final int TYPE_MESSAGE_IMAGE_FROM_ME = 7;
    public static final int TYPE_MESSAGE_IMAGE_FROM_FRIEND = 8;

    public static final int MODEL_PLAIN_MESSAGE = 50;
    public static final int MODEL_LOCATION_MESSAGE = 51;

    // Endpoint References
    public static final String MESSAGE_MODEL = "messageModel";
    public static final String MESSAGE_MAPMODEL = "mapContent";
    public static final String MESSAGE_USERID = "messageFromUserId";
    public static final String MESSAGE_CONTEXT = "messageText";
    public static final String MESSAGE_TIMESTAMP = "messageTimeStamp";

    private int messageType;
    private int messageModel;

    private String messageText;
    private String stickerUrl;
    private String messageFromRoomId;

    private String messageId;
    private String messageFromUserId;
    private String messageTimeStamp;
    private String messageLatitude;
    private String messageLongitude;

    private boolean isChecked = false;
    private boolean isWarning = false;
    private boolean mIsShowSentStatus = false;
    private boolean mIsShowDate = false;

    private MapContent mapContent;

    public Message() {}

    public Message(String roomId, String userId,
                   String content, String time, int model) {
        this.messageFromRoomId = roomId;
        this.messageFromUserId = userId;
        this.messageText = content;
        this.messageTimeStamp = time;
        this.messageModel = model;
    }

    public Message(String roomId, String userId,
                   MapContent mapContent, String time, int model) {
        this.messageFromRoomId = roomId;
        this.messageFromUserId = userId;
        this.mapContent = mapContent;
        this.messageTimeStamp = time;
        this.messageModel = model;
    }

    public MapContent getMapContent() {
        return mapContent;
    }

    public void setMapContent(MapContent mapContent) {
        this.mapContent = mapContent;
    }

    public int getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(int messageModel) {
        this.messageModel = messageModel;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageFromRoomId() {
        return messageFromRoomId;
    }

    public void setMessageFromRoomId(String messageFromRoomId) {
        this.messageFromRoomId = messageFromRoomId;
    }

    public String getMessageFromUserId() {
        return messageFromUserId;
    }

    public void setMessageFromUserId(String messageFromUserId) {
        this.messageFromUserId = messageFromUserId;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

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
        return messageType == TYPE_MESSAGE_FROM_ME
                || messageType == TYPE_STICKER_FROM_ME
                || messageType == TYPE_MESSAGE_IMAGE_FROM_ME;
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
        return mViewType == TYPE_STICKER_FROM_ME
                || mViewType == TYPE_MESSAGE_FROM_ME
                || mViewType == TYPE_MESSAGE_IMAGE_FROM_ME;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
