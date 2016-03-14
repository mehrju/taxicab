package com.xc0ffeelabs.taxicab.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.xc0ffeelabs.taxicab.models.User;

import java.util.List;

public class NearbyDrivers {

    public interface QueryDriversCallback {
        void onDriverLocationUpdate(List<User> users);
        void onFailed();
    }

    private LatLng mLocation;
    private int mMiles;
    private QueryDriversCallback mListener;

    private final static int DEFAULT_INTERVAL = 5; // in secs
    private final static int MSG_REFRESH_LOCATION = 1;

    private boolean mUpdateRequested = false;
    private Handler mHandler;
    private int mRefreshInterval = DEFAULT_INTERVAL;

    private static NearbyDrivers mDrivers;

    public static NearbyDrivers get() {
        if (mDrivers == null) {
            mDrivers = new NearbyDrivers();
        }
        return mDrivers;
    }

    private NearbyDrivers() {
        mHandler = new MyHandler(Looper.getMainLooper());
    }

    public synchronized void setLocation(LatLng location) {
        mLocation = location;
    }

    public void setRadius(int miles) {
        mMiles = miles;
    }

    public void setQueryDriversCallback(QueryDriversCallback callback) {
        mListener = callback;
    }

    public void startQueryDriverLocationUpdates() {
        mUpdateRequested = true;
        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LOCATION, 0);
    }

    synchronized public void stopQueryDriverLocationUpdates() {
        mUpdateRequested = false;
        mRefreshInterval = DEFAULT_INTERVAL;
        mListener = null;
        mLocation = null;
        mMiles = 0;
    }

    public void setRefreshInterval(int interval) {
        mRefreshInterval = interval;
    }

    public void getNow() {
        mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LOCATION, 0);
    }

    synchronized private void queryNearByDrivers() {
        ParseGeoPoint userLocation = new ParseGeoPoint(mLocation.latitude, mLocation.longitude);
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.ROLE, User.DRIVER_ROLE);
        query.whereWithinMiles("currentLocation", userLocation, mMiles);
        query.findInBackground(new FindCallback<User>() {
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    mListener.onDriverLocationUpdate(objects);
                } else {
                    mListener.onFailed();
                }
            }
        });
    }

    private class MyHandler extends Handler {

        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!mUpdateRequested) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_LOCATION:
                    queryNearByDrivers();
                    sendEmptyMessageDelayed(MSG_REFRESH_LOCATION, mRefreshInterval * 1000);
                    break;
                default:
                    throw new UnsupportedOperationException("Can't handle : " + msg.what);
            }
        }
    }
}
