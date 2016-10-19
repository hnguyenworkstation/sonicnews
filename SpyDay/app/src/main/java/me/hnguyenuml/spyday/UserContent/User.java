package me.hnguyenuml.spyday.UserContent;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hung Nguyen on 10/16/2016.
 */

public class User {
    private String UserUID;
    private String UserName;
    private String UserNickName;
    private String UserProfileURL;
    private LatLng UserCurrentLocation;
    private LatLng UserVisitingLocation;

    public User() {}

    public User(String UID) {
        this.UserUID = UID;
    }

    public User(String UID, String name, String nickname, String ProfileURL) {

    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserProfileURL() {
        return UserProfileURL;
    }

    public void setUserProfileURL(String userProfileURL) {
        UserProfileURL = userProfileURL;
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
