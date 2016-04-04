package com.xc0ffeelabs.taxicab.models;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

@ParseClassName("_User")
public class User extends ParseUser {

    public enum UserStates {
        Online,
        WaitingDriver,
        EnrouteDest,
        DstReached
    }

    public static final String ROLE = "role";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String LICENSE = "license";
    public static final String CAR_MODEL = "carModel";
    public static final String CAR_NUMBER = "carNumber";
    public static final String CURRENT_LOCATION = "currentLocation";
    public static final String STATE = "state";
    public static final String PICKUP_LOCATION = "pickUpLocation";
    public static final String DEST_LOCATION = "destLocation";
    public static final String DRIVER_START_LOCATION = "driverStartLocation";
    private static final String IS_FACEBOOK_LOGIN = "fbLogin";
    private static final String IS_VERIFIED = "fbUserVerified";

    public static final String USER_ROLE = "user";
    public static final String DRIVER_ROLE = "driver";
    public static final String PROFILE_IMAGE = "profileImage";

    private String travelTimeText;
    private long travelTime;

    public User() {
    }

    public void setRole(String role) {
        put(ROLE, role);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public void setPhone(String phone) {
        put(PHONE, phone);
    }

    public void setLicense(String license) {
        put(LICENSE, license);
    }

    public void setCarModel(String carModel) {
        put(CAR_MODEL, carModel);
    }

    public void setCarNumber(String carNumber) {
        put(CAR_NUMBER, carNumber);
    }

    public String getName() {
        return getString(NAME);
    }

    public String getEmail() {
        return getUsername();
    }

    public String getRole() {
        return getString(ROLE);
    }

    public String getPhone() {
        return getString(PHONE);
    }

    public String getLicense() {
        return getString(LICENSE);
    }

    public String getCarModel() {
        return getString(CAR_MODEL);
    }

    public String getCarNumber() {
        return getString(CAR_NUMBER);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(CURRENT_LOCATION);
    }

    public LatLng getPosition() {
        return new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
    }

    public String getTravelTimeText() {
        return travelTimeText;
    }

    public void setTravelTimeText(String travelTimeText) {
        this.travelTimeText = travelTimeText;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
        this.travelTime = travelTime;
    }

    public UserStates getUserState() {
        String state = getString("state");
        if (TextUtils.isEmpty(state)) {
            return UserStates.Online;
        }
        return UserStates.valueOf(state);
    }

    public void setUserState(UserStates state) {
        put("state", state.toString());
    }

    public void setDriverId(String id) {
        put("driverId", id);
    }

    public String getDriverId() {
        return getString("driverId");
    }

    public void setPickupLocation(LatLng location, String text, final SaveCallback callback) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        if (text != null) {
            pnt.setText(text);
        }
        put(PICKUP_LOCATION, pnt);
        saveInBackground(callback);
    }

    public void setDestLocation(LatLng location, String text) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        if (!TextUtils.isEmpty(text)) {
            pnt.setText(text);
        }
        put(DEST_LOCATION, pnt);
        saveInBackground();
    }

    public Location getDestLocation() {
        return (Location) get(DEST_LOCATION);
    }

    public Location getPickUpLocation() {
        return  (Location) get(PICKUP_LOCATION);
    }

    public void setDriverStartLocation(LatLng location, final SaveCallback callback) {
        Location pnt = new Location();
        pnt.setLatitude(location.latitude);
        pnt.setLongitude(location.longitude);
        put(DRIVER_START_LOCATION, pnt);
        saveInBackground(callback);
    }

    public Location getDriverStartLocation() {
        return  (Location) get(DRIVER_START_LOCATION);
    }

    public String getProfileImage(){
        String imageUrl =  getString(PROFILE_IMAGE);
        if (imageUrl != null && imageUrl.length() > 0) {
            return imageUrl;
        }
        return null;
    }

    public void setProfieImage(String url) {
        put(PROFILE_IMAGE, url);
    }

    public void setIsFbLogin(boolean isFbLogin) {
        put(IS_FACEBOOK_LOGIN, isFbLogin);
    }

    public boolean getIsFbLogin() {
        return getBoolean(IS_FACEBOOK_LOGIN);
    }

    public void setIsUserVerified(boolean isUserVerified) {
        put(IS_VERIFIED, isUserVerified);
    }

    public boolean getIsUserVerified() {
        return getBoolean(IS_VERIFIED);
    }
}
