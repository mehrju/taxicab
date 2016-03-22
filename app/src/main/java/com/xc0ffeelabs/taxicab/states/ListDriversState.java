package com.xc0ffeelabs.taxicab.states;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.fragments.ControlsFragment;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.LocationDetails;
import com.xc0ffeelabs.taxicab.network.NearbyDrivers;
import com.xc0ffeelabs.taxicab.network.TravelTime;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListDriversState implements State {

    private final static int REFRESH_INTERVAL = 10;

    private Map<String, Marker> mMarkerMap = new HashMap<>();
    private LatLng mUserLocation;
    private NearbyDrivers mNearbyDrivers;
    private volatile boolean mSortRequested = true;
    protected List<User> mSortedUsers = new ArrayList<>();
    private MapsActivity mActivity;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;

    private static ListDriversState mListDriverState;

    public static ListDriversState getInstance() {
        if (mListDriverState == null) {
            mListDriverState = new ListDriversState();
        }
        return mListDriverState;
    }

    private ListDriversState() {
    }

    @Override
    public void enterState(MapsActivity activity, Bundle data) {
        mActivity = activity;
        mMap = mActivity.getMap();
        mApiClient = mActivity.getApiClient();
        initialize();
    }

    private void initialize() {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm_controls, ControlsFragment.getInstance(), "controls");
        ft.commit();
        ControlsFragment.getInstance().setControlsInteraction(new ControlsFragment.ControlsInteraction() {
            @Override
            public void onPickupButtonClicked() {
                requestToPickup();
            }
        });

        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
            if (location != null) {
                mUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                new GetLocation(mUserLocation, new GetCallback() {
                    @Override
                    public void done() {
                        ControlsFragment.getInstance().animateShowControls();
                    }
                }).execute();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mUserLocation, 12);
                mMap.moveCamera(cameraUpdate);
                startLocationUpdates();
                fetchNearByDrivers();
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        repositionCamera(cameraPosition);
                    }
                });
            } else {
                Toast.makeText(mActivity, "Cound't retrieve current location. Please enable GPS location",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            throw e;
        }
    }

    private void requestToPickup() {
        Bundle b = new Bundle();
        List<String> drivers = new ArrayList<>();
        for (User driver : mSortedUsers) {
            drivers.add(driver.getObjectId());
        }
        PickupRequestedState.PickupRequestData data =
                new PickupRequestedState.PickupRequestData(
                        ParseUser.getCurrentUser().getObjectId(),
                        mUserLocation,
                        drivers);
        b.putParcelable(PickupRequestedState.PickupRequestData.PICKUP_DATA, Parcels.wrap(data));
        TaxiCabApplication.getStateManager().startState(StateManager.States.TripRequested, b);
    }

    private void repositionCamera(CameraPosition cameraPosition) {
        displayAproximateTime("--");
        mNearbyDrivers.setLocation(cameraPosition.target);
        mNearbyDrivers.getNow();
        mUserLocation = cameraPosition.target;
        new GetLocation(mUserLocation, null).execute();
        mSortRequested = true;
    }

    private void startLocationUpdates() {
    }

    private void fetchNearByDrivers() {
        mNearbyDrivers = TaxiCabApplication.getNearbyDrivers();
        mNearbyDrivers.setLocation(mUserLocation);
        mNearbyDrivers.setRadius(20);
        mNearbyDrivers.setRefreshInterval(REFRESH_INTERVAL);
        mNearbyDrivers.setQueryDriversCallback(new NearbyDrivers.QueryDriversCallback() {
            @Override
            public void onDriverLocationUpdate(final List<User> users) {
                if (users.size() <= 0) {
                    ControlsFragment.getInstance().setPickupEnabled(false);
                    addDriverMarkers(users);
                } else {
                    ControlsFragment.getInstance().setPickupEnabled(true);
                    addDriverMarkers(users);
                    if (mSortRequested) {
                        TravelTime.compute(mUserLocation, users, new TravelTime.TravelTimeComputed() {
                            @Override
                            public void onTravelTimeComputed(List<User> drivers) {
                                mSortedUsers.clear();
                                mSortedUsers.addAll(users);
                                if (drivers != null && !drivers.isEmpty()) {
                                    displayAproximateTime(drivers.get(0).getTravelTimeText());
                                }
                                mSortRequested = false;
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(mActivity, "Failed to get nearby drivers", Toast.LENGTH_SHORT).show();
            }
        });
        mNearbyDrivers.startQueryDriverLocationUpdates();
    }

    private void displayAproximateTime(String text) {
        if (mSortedUsers != null && !mSortedUsers.isEmpty()) {
            ControlsFragment.getInstance().setApprTime(text);
        }
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
        mNearbyDrivers.stopQueryDriverLocationUpdates();
        mMap.setOnCameraChangeListener(null);
        Collection<Marker> driverMarkers = mMarkerMap.values();
        for (Marker marker : driverMarkers) {
            marker.remove();
        }
        mMarkerMap.clear();
    }

    @Override
    public StateManager.States getState() {
        return StateManager.States.ListDriver;
    }

    public interface GetCallback {
        void done();
    }

    private class GetLocation extends AsyncTask<Void, Void, Address> {

        private final LatLng mLocation;
        private final GetCallback mListener;

        public GetLocation(LatLng location, GetCallback listener) {
            mLocation = location;
            mListener = listener;
        }

        @Override
        protected Address doInBackground(Void... params) {
            return LocationDetails.getLocationDetails(mActivity, mLocation);
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address == null || address.getAddressLine(0) == null) {
                ControlsFragment.getInstance().setSourceLocation("--");
            } else {
                ControlsFragment.getInstance().setSourceLocation(address.getAddressLine(0));
            }
            if (mListener != null) {
                mListener.done();
            }
        }
    }
}
