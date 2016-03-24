package com.xc0ffeelabs.taxicab.states;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.fragments.TaxiEnrouteFragment;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.GMapV2Direction;

import org.parceler.Parcel;
import org.parceler.Parcels;
import org.w3c.dom.Document;

import java.util.ArrayList;

public class TaxiEnroute implements State {

    private static final int MSG_REFRESH_LOCATION = 1;
    private static final int REFRESH_INTERVAL = 7;  // in sec
    private static final String TAG = TaxiEnroute.class.getSimpleName();

    @Parcel
    public static class TaxiEnrouteData {

        public static final String ENROUTE_DATA = "enrouteData";

        LatLng mUserLocation;
        LatLng mDriverStartLoc;
        String mDriverId;

        public TaxiEnrouteData() {
        }

        public TaxiEnrouteData(LatLng userLocation,
                               LatLng driverStart,
                               String driverId) {
            mUserLocation = userLocation;
            mDriverStartLoc = driverStart;
            mDriverId = driverId;
        }
    }

    private static TaxiEnroute mTaxiEnroute;

    private MapsActivity mActivity;
    private TaxiEnrouteData mEnrouteData;
    private Marker mUserMarker;
    private Marker mDriverMarker;
    private GoogleMap mMap;
    private LatLng mUserLocation;
    private User mDriver;
    private LatLng mDriverLocation;
    private Handler mHandler = new MyHandler(Looper.getMainLooper());
    private boolean mRefreshRequested;
    private Polyline mLine;

    public static TaxiEnroute getInstance() {
        if (mTaxiEnroute == null) {
            mTaxiEnroute = new TaxiEnroute();
        }
        return mTaxiEnroute;
    }

    private TaxiEnroute() {
    }

    @Override
    public void enterState(MapsActivity activity, Bundle data) {
        mActivity = activity;
        mEnrouteData = Parcels.unwrap(
                data.getParcelable(TaxiEnrouteData.ENROUTE_DATA));
        mUserLocation = mEnrouteData.mUserLocation;
        if (mEnrouteData == null) {
            throw new IllegalArgumentException("Why? Why? are you sending null data. I'm tired");
        }
        initialize();
    }

    @Override
    public void exitState() {
        stopDriverLocationUpdate();
        mUserMarker.remove();
        mDriverMarker.remove();
        if (mLine != null) {
            mLine.remove();
        }
    }

    public void stopDriverLocationUpdate() {
        mRefreshRequested = false;
    }

    private void initialize() {
        mMap = mActivity.getMap();

        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm_controls, TaxiEnrouteFragment.getInstance(), "enroute");
        ft.commit();

        addUserMarker();
        fetchDriverDetails();

        updateStateInServer();
    }

    private void updateStateInServer() {
        User user = (User) ParseUser.getCurrentUser();
        user.setUserState(User.UserStates.WaitingDriver);
        user.setDriverId(mEnrouteData.mDriverId);
        user.saveInBackground();
    }

    private void addUserMarker() {
        if (mUserMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mUserLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                    .title("Me");
            mUserMarker = mMap.addMarker(markerOptions);
        } else {
            mUserMarker.setPosition(mUserLocation);
        }
    }

    private void fetchDriverDetails() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.getInBackground(mEnrouteData.mDriverId, new GetCallback<User>() {
            @Override
            public void done(User object, ParseException e) {
                TaxiEnrouteFragment.getInstance().hideProgress();
                addDriverMarker(object);
            }
        });
    }

    private void addDriverMarker(User driver) {
        setDriverDetails(driver);
        mDriver = driver;
        mDriverLocation = new LatLng(mDriver.getLocation().getLatitude(),
                mDriver.getLocation().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().title(driver.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_pin))
                .position(mDriverLocation);
        mDriverMarker = mMap.addMarker(markerOptions);
        zoomCamera();
    }

    private void setDriverDetails(User driver) {
        TaxiEnrouteFragment.getInstance().setDriverName(driver.getName());
        TaxiEnrouteFragment.getInstance().setCarName(driver.getCarModel());
        TaxiEnrouteFragment.getInstance().setImage(driver.getProfileImage());
    }

    private void zoomCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mUserLocation);
        builder.include(mEnrouteData.mDriverStartLoc);
        LatLngBounds bounds = builder.build();
        int padding  = (int) mActivity.getResources().getDimension(R.dimen.map_offset);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        showRoute();
        updateDriverLocation();
    }

    private void updateDriverLocation() {
        mRefreshRequested = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_LOCATION);
    }

    private void showRoute() {
        new GMapV2Direction(mEnrouteData.mDriverStartLoc, mUserLocation, new GMapV2Direction.RouteReady() {
            @Override
            public void onRouteReady(Document document) {
                drawRoute(document);
            }
        }).execute();
    }

    private void drawRoute(Document doc) {
        if (doc != null) {
            ArrayList<LatLng> directionPoint = GMapV2Direction.getDirection(doc);
            PolylineOptions rectLine = new PolylineOptions().width(10).color(
                    Color.parseColor("#4285f4"));

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }
            mLine = mMap.addPolyline(rectLine);
        }
    }

    private class MyHandler extends Handler {

        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LOCATION:
                    if (mRefreshRequested) {
                        fetchDriverPosition();
                        sendEmptyMessageDelayed(MSG_REFRESH_LOCATION, REFRESH_INTERVAL * 1000);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Can't handle");
            }
        }
    }

    private void fetchDriverPosition() {
        mDriver.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    updateDriverMarker();
                } else {
                    Log.e(TAG, "Unable to update driver location");
                }
            }
        });
    }

    private void updateDriverMarker() {
        mDriverLocation = new LatLng(mDriver.getLocation().getLatitude(),
                mDriver.getLocation().getLongitude());
        mDriverMarker.setPosition(mDriverLocation);
    }

    @Override
    public StateManager.States getState() {
        return StateManager.States.TaxiEnroute;
    }

    @Override
    public void onTouchUp() {

    }

    @Override
    public void onTouchDown() {

    }
}
