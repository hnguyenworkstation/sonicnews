package me.hnguyenuml.spyday.UserContent;

/**
 * Created by Hung Nguyen on 11/2/2016.
 */

public class MapContent {

    private String latitude;
    private String longitude;

    public MapContent() {
    }

    public MapContent(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
