package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicab.activities.MapsActivity;

public class StateManager {

    public enum States {
        ListDriver,
        TripRequested,
        TaxiEnroute,
        TaxiArrived,
        DestEnroute,
        DestArrived,
        TripEnded
    }

    private static StateManager ourInstance;

    private State mCurrentState = null;
    private MapsActivity mAcitivity;

    public static StateManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new StateManager();
        }
        return ourInstance;
    }

    private StateManager() {
    }

    public void setActivity(MapsActivity activity) {
        mAcitivity = activity;
    }

    public void startState(States state, Bundle data) {
        if (mCurrentState != null) {
            mCurrentState.exitState();
        }

        switch (state) {
            case ListDriver:
                mCurrentState = ListDriversState.getInstance();
                break;
            case TripRequested:
                mCurrentState = PickupRequestedState.getInstance();
                break;
            case TaxiEnroute:
                mCurrentState = TaxiEnroute.getInstance();
                break;
            case DestEnroute:
                mCurrentState = EnrouteToDstState.getInstance();
                break;
            default:
                throw new UnsupportedOperationException("No such state");
        }
        mCurrentState.enterState(mAcitivity, data);
    }
}
