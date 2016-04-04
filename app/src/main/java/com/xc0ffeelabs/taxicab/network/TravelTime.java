package com.xc0ffeelabs.taxicab.network;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.xc0ffeelabs.taxicab.models.DirectionsResponse;
import com.xc0ffeelabs.taxicab.models.Duration;
import com.xc0ffeelabs.taxicab.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TravelTime {

    private static final int MAX_DRIVERS = 5;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    public interface TravelTimeComputed {
        void onTravelTimeComputed(List<User> drivers);
    }

    public static void compute(final LatLng src, final List<User> drivers,
                        final TravelTimeComputed listener) {
        final int minDrivers = Math.min(drivers.size(), MAX_DRIVERS);
        final List<User> sortedUsers = new ArrayList<>(minDrivers);
        for (final User user : drivers) {
            String url = directionsUrl(src, user.getPosition());
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    user.setTravelTimeText("-");
                    user.setTravelTime(Long.MAX_VALUE);
                    sortedUsers.add(user);
                    if (sortedUsers.size() >= minDrivers) {
                        sortUsers(sortedUsers, listener);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Gson gson = new GsonBuilder().create();
                    DirectionsResponse response = gson.fromJson(responseString, DirectionsResponse.class);
                    if (response.routes != null &&
                            response.routes.size() > 0 &&
                            response.routes.get(0).legs != null &&
                            response.routes.get(0).legs.size() > 0 &&
                            response.routes.get(0).legs.get(0).duration != null) {
                        Duration duration = response.routes.get(0).legs.get(0).duration;
                        user.setTravelTime(duration.value);
                        user.setTravelTimeText(duration.text);
                    }
                    sortedUsers.add(user);
                    if (sortedUsers.size() >= minDrivers) {
                        sortUsers(sortedUsers, listener);
                    }
                }
            });
        }
    }

    private static void sortUsers(List<User> sortedUsers, final TravelTimeComputed listener) {
        Collections.sort(sortedUsers, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getTravelTime() < rhs.getTravelTime() ? -1 : 1;
            }
        });
        listener.onTravelTimeComputed(sortedUsers);
    }

    private static String directionsUrl(LatLng src, LatLng dest) {
        return BASE_URL + origin(src) + "&" + dest(dest);
    }

    private static String origin(LatLng position) {
        return "origin=" + position.latitude + "," + position.longitude;
    }

    private static String dest(LatLng position) {
        return "destination=" + position.latitude + "," + position.longitude;
    }

}
