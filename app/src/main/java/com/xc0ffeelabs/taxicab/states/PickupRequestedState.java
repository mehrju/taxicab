package com.xc0ffeelabs.taxicab.states;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.fragments.TripRequested;
import com.xc0ffeelabs.taxicab.network.ParseEndPoints;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

public class PickupRequestedState implements State {

    private static final int MSG_FETCH_STATE = 1;
    private static final int REFRESH_INTERVAL = 5; // in sec
    /* We will wait for max 20sec in this state. If no response within 20 sec,
    *  we will timeout */
    private static final int MAX_WAIT = 60; // in sec
    private static final int MAX_RETRY = MAX_WAIT / 5;
    private static final String TRIP_OBJECT_ID = "tripObjectId";

    private int mRetryCnt = 0;

    @Parcel
    public static class PickupRequestData {
        public static final String PICKUP_DATA = "pickupData";

        String mUserId;
        LatLng mUserLocation;
        List<String> mDriverIds;

        public PickupRequestData() {
        }

        public PickupRequestData(String userId,
                                 LatLng userLocation,
                                 List<String> driverIds) {
            mUserId = userId;
            mUserLocation = userLocation;
            mDriverIds = driverIds;
        }
    }

    private MapsActivity mActivity;
    private String mUserId;
    private LatLng mUserLocation;
    private List<String> mDriverIds;
    private Handler mHandler = new MyHandler(Looper.myLooper());

    private static PickupRequestedState mPickupRequestedState;

    public static PickupRequestedState getInstance() {
        if (mPickupRequestedState == null) {
            mPickupRequestedState = new PickupRequestedState();
        }
        return mPickupRequestedState;
    }

    private PickupRequestedState() {
    }

    @Override
    public void enterState(MapsActivity activity, Bundle data) {
        mActivity = activity;
        PickupRequestData pickupRequestData =
                Parcels.unwrap(data.getParcelable(PickupRequestData.PICKUP_DATA));
        mUserId = pickupRequestData.mUserId;
        mUserLocation = pickupRequestData.mUserLocation;
        mDriverIds = pickupRequestData.mDriverIds;
        initialize();
    }

    private void initialize() {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm_controls, TripRequested.newInstance(), "controls");
        ft.commit();
        ParseEndPoints.initiateTrip(mUserId, mDriverIds, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Log.d("NAYAN", "Trip id = " + object.getObjectId());
                object.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            Log.d("NAYAN", "trip id = " + object.getObjectId());
                            statusChangeMonitor(object);
                        } else {
                            Log.d("NAYAN", "Something went wrong");
                        }
                    }
                });/*
                if (e == null) {
                    statusChangeMonitor(object);
                } else {
                    Toast.makeText(mActivity, "Sorry, something went wrong while booking. Please try again",
                            Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void statusChangeMonitor(ParseObject object) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(TRIP_OBJECT_ID, object.getObjectId());
        msg.what = MSG_FETCH_STATE;
        msg.setData(data);
        /* TODO: Uncomment the below line in the final version */
        //mHandler.sendMessage(msg);
        mHandler.sendMessageDelayed(msg, 3000);
    }

    private void fetchState(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    String status = object.getString("status");
                    if (status.equals("confirmed")) {
                        tripConfirmed(object.getParseObject("driver"));
                    } else {
                        retry(objectId);
                    }
                } else {
                    retry(objectId);
                }
            }
        });
    }

    private void retry(String objectId) {
        mRetryCnt++;
        if (mRetryCnt < MAX_RETRY) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString(TRIP_OBJECT_ID, objectId);
            msg.what = MSG_FETCH_STATE;
            msg.setData(data);
            mHandler.sendMessageDelayed(msg, REFRESH_INTERVAL * 1000);
        } else {
            goBackToListDrivers();
        }
    }

    private void goBackToListDrivers() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.title_unable_to_book)
                .setMessage(R.string.msg_unable_to_book)
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaxiCabApplication.getStateManager().startState(StateManager.States.ListDriver, null);
                    }
                })
                .create().show();
    }

    private void tripConfirmed(ParseObject driver) {
        Bundle data = new Bundle();
        TaxiEnroute.TaxiEnrouteData enrouteData =
                new TaxiEnroute.TaxiEnrouteData(mUserLocation, driver.getObjectId());
        data.putParcelable(TaxiEnroute.TaxiEnrouteData.ENROUTE_DATA, Parcels.wrap(enrouteData));
        TaxiCabApplication.getStateManager().startState(StateManager.States.TaxiEnroute, data);
    }

    @Override
    public void exitState() {

    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FETCH_STATE:
                    Bundle data = msg.getData();
                    String objectId = data.getString(TRIP_OBJECT_ID);
                    fetchState(objectId);
                    break;
                default:
                    throw new UnsupportedOperationException("Can't handle : " + msg.what);
            }
        }
    }
}
