package com.xc0ffeelabs.taxicab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject {

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";
    public static final String LOCTEXT = "text";

    public Location() {
    }

    public void setLatitude(double latitude) {
        put(LATITUDE, latitude);
    }

    public void setLongitude(double longitude) {
        put(LONGITUDE, longitude);
    }

    public void setText(String text) { put(LOCTEXT, text); }

    public double getLatitude() {
        return getDouble(LATITUDE);
    }

    public double getLongitude() {
        return getDouble(LONGITUDE);
    }

    public String getText() { return getString(LOCTEXT); }
}
