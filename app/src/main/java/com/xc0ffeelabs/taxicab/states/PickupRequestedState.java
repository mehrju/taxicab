package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.fragments.TripRequested;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupRequestedState implements State {

    @Parcel
    public static class PickupRequestData {
        public static final String PICKUP_DATA = "pickupData";

        String mUserId;
        List<String> mDriverIds;

        public PickupRequestData() {
        }

        public PickupRequestData(String userId,
                                 List<String> driverIds) {
            mUserId = userId;
            mDriverIds = driverIds;
        }
    }

    private MapsActivity mActivity;
    private String mUserId;
    private List<String> mDriverIds;

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
        mDriverIds = pickupRequestData.mDriverIds;
        initialize();
    }

    private void initialize() {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm_controls, TripRequested.newInstance(), "controls");
        ft.commit();

        Map<String, String> request = new HashMap<>();
        request.put("userId", mUserId);
        request.put("driverId", mDriverIds.get(0));
        ParseCloud.callFunctionInBackground("initiateTrip", request, new FunctionCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Log.d("NAYAN", "Done!. Received response");
            }
        });
    }

    @Override
    public void exitState() {

    }
}
