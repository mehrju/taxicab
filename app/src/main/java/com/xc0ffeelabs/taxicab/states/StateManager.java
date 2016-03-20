package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.models.User;

public class StateManager {

    private static final String TAG = StateManager.class.getSimpleName();

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

    /* Do not call this method without showing progress bar. */
    public void startDefaultState() {
        User user = (User) ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<User>() {
            @Override
            public void done(User object, ParseException e) {
                if (e == null) {
                    switchToDefaultState(object);
                } else {
                    Log.d(TAG, "Something went wrong while fetching.");
                }
            }
        });
    }

    private void switchToDefaultState(User user) {
        User.UserStates state = user.getUserState();
        switch (state) {
            case Online:
                startState(States.ListDriver, null);
                break;
        }
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
