package com.xc0ffeelabs.taxicab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Location")
public class Location extends ParseObject{

    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    double latitude;
    double longitude;
}
