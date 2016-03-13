package com.xc0ffeelabs.taxicab.managers;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.xc0ffeelabs.taxicab.fragments.MapsFragment;

public class StateManager implements MapsFragment.MapReady {

    public enum States {
        ListDriver,
        TaxiEnroute,
        TaxiArrived,
        DestEnroute,
        DestArrived,
        TripEnded
    }

    private static StateManager ourInstance;

    private GoogleMap mMap = null;
    private GoogleApiClient mClient = null;
    private State mCurrentState = null;
    private boolean mStartWhenReadyState = false;

    public static StateManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StateManager();
        }
        return ourInstance;
    }

    private StateManager() {
    }

    @Override
    public void onMapReady(GoogleMap map, GoogleApiClient apiClient) {
        mMap = map;
        mClient = apiClient;
        if (mStartWhenReadyState) {
            mCurrentState.enterState(map, apiClient);
            mStartWhenReadyState = false;
        }
    }

    public void startState(States state) {
        if (mCurrentState != null) {
            mCurrentState.exitState();
        }

        switch (state) {
            case ListDriver:
                mCurrentState = new ListDriversState();
                if (mMap == null) {
                    mStartWhenReadyState = true;
                } else {
                    mCurrentState.enterState(mMap, mClient);
                }
                break;
            default:
                throw new UnsupportedOperationException("No such state");
        }
    }
}
