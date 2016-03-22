package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.fragments.DestReachedFragment;
import com.xc0ffeelabs.taxicab.models.User;

public class DestReached implements State{

    private static DestReached mDestReached;

    public static DestReached getInstance() {
        if (mDestReached == null) {
            mDestReached = new DestReached();
        }
        return mDestReached;
    }

    private MapsActivity mActivity;

    private DestReached() {
    }

    @Override
    public void enterState(MapsActivity activity, Bundle data) {
        mActivity = activity;
        initialize();
    }

    private void initialize() {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fm_controls, DestReachedFragment.getInstance(), "dstReached");
        ft.commitAllowingStateLoss();

        DestReachedFragment.getInstance().setTripEndInteraction(new DestReachedFragment.TripEndInteraction() {
            @Override
            public void onTripEnded() {
                endTrip();
            }
        });
    }

    private void endTrip() {
        User user = (User) ParseUser.getCurrentUser();
        user.setUserState(User.UserStates.Online);
        user.saveInBackground();
        StateManager.getInstance().startState(StateManager.States.ListDriver, null);
    }

    @Override
    public void exitState() {

    }

    @Override
    public StateManager.States getState() {
        return StateManager.States.DestArrived;
    }

    @Override
    public void onTouchUp() {

    }

    @Override
    public void onTouchDown() {

    }
}
