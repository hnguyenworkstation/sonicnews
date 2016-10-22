package me.hnguyenuml.spyday.UserContent;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hung Nguyen on 10/16/2016.
 */

public class User {
    private String user_uid;
    private String user_name;
    private String user_nickname;
    private String user_profileurl;
    private LatLng UserCurrentLocation;
    private LatLng UserVisitingLocation;

    public User() {}

    public User(String UID) {
        this.user_uid = UID;
    }

    public User(String UID, String name, String nickname, String ProfileURL) {
        this.user_uid = UID;
        this.user_name = name;
        this.user_nickname = nickname;
        this.user_profileurl = ProfileURL;
    }

    public String getUserNickName() {
        return user_nickname;
    }

    public void setUserNickName(String userNickName) {
        user_nickname = userNickName;
    }

    public String getUserUID() {
        return user_uid;
    }

    public void setUserUID(String userUID) {
        user_uid = userUID;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String userName) {
        user_name = userName;
    }

    public String getUserProfileURL() {
        return user_profileurl;
    }

    public void setUserProfileURL(String userProfileURL) {
        user_profileurl = userProfileURL;
    }

    public LatLng getUserCurrentLocation() {
        return UserCurrentLocation;
    }

    public void setUserCurrentLocation(LatLng userCurrentLocation) {
        UserCurrentLocation = userCurrentLocation;
    }

    public LatLng getUserVisitingLocation() {
        return UserVisitingLocation;
    }

    public void setUserVisitingLocation(LatLng userVisitingLocation) {
        UserVisitingLocation = userVisitingLocation;
    }

}
