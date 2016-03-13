package com.xc0ffeelabs.taxicab.managers;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.NearbyDrivers;
import com.xc0ffeelabs.taxicab.network.TravelTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListDriversState implements State {

    private final static int REFRESH_INTERVAL = 10;

    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Map<String, Marker> mMarkerMap = new HashMap<>();
    private Context mContext;
    private List<User> mSortedUsers;
    private LatLng mUserLocation;

    public ListDriversState() {
        mContext = TaxiCabApplication.get().getAppContext();
    }

    @Override
    public void enterState(GoogleMap map, GoogleApiClient client) {
        mMap = map;
        mApiClient = client;
        initialize();
    }

    private void initialize() {
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (location != null) {
                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mUserLocation, 10);
                mMap.animateCamera(cameraUpdate);
                startLocationUpdates();
                fetchNearByDrivers();
            } else {
                Toast.makeText(mContext, "Cound't retrieve current location. Please enable GPS location",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            throw e;
        }
    }

    private void startLocationUpdates() {
    }

    private void fetchNearByDrivers() {
        final NearbyDrivers nearbyDrivers = TaxiCabApplication.getNearbyDrivers();
        nearbyDrivers.setLocation(mUserLocation);
        nearbyDrivers.setRadius(20);
        nearbyDrivers.setRefreshInterval(REFRESH_INTERVAL);
        nearbyDrivers.setQueryDriversCallback(new NearbyDrivers.QueryDriversCallback() {
            @Override
            public void onDriverLocationUpdate(List<User> users) {
                if (users.size() <= 0) {
                    Toast.makeText(mContext, "No nearby drivers found", Toast.LENGTH_SHORT).show();
                } else {
                    addDriverMarkers(users);
                    if (mSortedUsers == null) {
                        TravelTime.compute(mUserLocation, users, new TravelTime.TravelTimeComputed() {
                            @Override
                            public void onTravelTimeComputed(List<User> drivers) {
                                mSortedUsers = drivers;
                                displayAproximateTime();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(mContext, "Failed to get nearby drivers", Toast.LENGTH_SHORT).show();
            }
        });
        nearbyDrivers.startQueryDriverLocationUpdates();
    }

    private void displayAproximateTime() {
        Log.d("NAYAN", "Appr time = " + mSortedUsers.get(0).getTravelTimeText());
    }

    private void addDriverMarkers(List<User> drivers) {
        Set<String> mCurrentDrivers = new HashSet<>(mMarkerMap.keySet());
        for (User driver : drivers) {
            LatLng position = new LatLng(driver.getLocation().getLatitude(), driver.getLocation().getLongitude());
            if (!mMarkerMap.containsKey(driver.getObjectId())) {
                MarkerOptions markerOptions = new MarkerOptions().position(position).title(driver.getName());
                Marker marker = mMap.addMarker(markerOptions);
                mMarkerMap.put(driver.getObjectId(), marker);
            } else {
                Marker marker = mMarkerMap.get(driver.getObjectId());
                marker.setPosition(position);
            }
            mCurrentDrivers.remove(driver.getObjectId());
        }
        for (String id: mCurrentDrivers) {
            Marker marker = mMarkerMap.get(id);
            marker.remove();
            mMarkerMap.remove(id);
        }
    }

    @Override
    public void exitState() {

    }
}
