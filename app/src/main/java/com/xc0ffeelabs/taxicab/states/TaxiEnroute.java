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
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.fragments.TaxiEnrouteFragment;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.GMapV2Direction;
import com.xc0ffeelabs.taxicab.stubs.NavigateDriverToUserStub;

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
        String mDriverId;

        public TaxiEnrouteData() {
        }

        public TaxiEnrouteData(LatLng userLocation,
                               String driverId) {
            mUserLocation = userLocation;
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
    }

    private void addUserMarker() {
        if (mUserMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mUserLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_grey600_48dp))
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
        mDriver = driver;
        mDriverLocation = new LatLng(mDriver.getLocation().getLatitude(),
                mDriver.getLocation().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().title(driver.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_grey600_48dp))
                .position(mDriverLocation);
        mDriverMarker = mMap.addMarker(markerOptions);
        zoomCamera();
    }

    private void zoomCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mUserLocation);
        builder.include(mDriverLocation);
        LatLngBounds bounds = builder.build();
        int padding  = (int) mActivity.getResources().getDimension(R.dimen.map_offset);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
        showRoute();
        updateDriverLocation();

        /* TODO: Remove this for final app */
        new NavigateDriverToUserStub(mDriverLocation, mUserLocation, mDriver).run();
    }

    private void updateDriverLocation() {
        mRefreshRequested = true;
        mHandler.sendEmptyMessage(MSG_REFRESH_LOCATION);
    }

    private void showRoute() {
        new GMapV2Direction(mDriverLocation, mUserLocation, new GMapV2Direction.RouteReady() {
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
                    Color.BLUE);

            for (int i = 0; i < directionPoint.size(); i++) {
                rectLine.add(directionPoint.get(i));
            }
            mMap.addPolyline(rectLine);
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
}
