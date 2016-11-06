package me.hnguyenuml.spyday.UserContent;

/**
 * Created by Hung Nguyen on 11/4/2016.
 */

public class Event {

    public static final int TYPE_IMAGE_EVENT = 0;

    public static final String EVENT_DATABASE = "Events";
    public static final String EVENT_ID = "id";
    public static final String EVENT_NAME = "name";
    public static final String EVENT_STATUS = "status";
    public static final String EVENT_IMAGE = "image";
    public static final String EVENT_PROFILEPIC = "profilePic";
    public static final String EVENT_TIMESTAMP = "timeStamp";
    public static final String EVENT_URL = "url";
    public static final String EVENT_LIKECOUNT = "likeCount";
    public static final String EVENT_COMMENTCOUNT = "commentCount";
    public static final String EVENT_STORAGE = "Event_Images";

    private String id;
    private String name;
    private String status;
    private String image;
    private String profilePic;
    private String timeStamp;
    private String url;
    private int likecount;
    private int commentcount;

    private int type;

    public Event() {
    }

    public Event(String id, String name, String image, String status,
                    String profilePic, String timeStamp, String url, int likecount, int commentcount) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.likecount = likecount;
        this.commentcount = commentcount;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public int getType() {
        return TYPE_IMAGE_EVENT;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
