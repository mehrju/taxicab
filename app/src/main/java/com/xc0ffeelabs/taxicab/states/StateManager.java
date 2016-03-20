package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.models.Location;
import com.xc0ffeelabs.taxicab.models.User;

import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

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

    private void switchToDefaultState(final User user) {
        User.UserStates state = user.getUserState();
        switch (state) {
            case Online:
                startState(States.ListDriver, null);
                break;
            case WaitingDriver:
                List<ParseObject> objects = new LinkedList<>();
                final Location location = user.getPickUpLocation();
                final Location driverStartLoc = user.getDriverStartLocation();
                objects.add(location);
                objects.add(driverStartLoc);
                ParseObject.fetchAllInBackground(objects, new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            Bundle bundle = new Bundle();
                            TaxiEnroute.TaxiEnrouteData data = new TaxiEnroute.TaxiEnrouteData(
                                    new LatLng(location.getLatitude(), location.getLongitude()),
                                    new LatLng(driverStartLoc.getLatitude(), driverStartLoc.getLongitude()),
                                    user.getDriverId());
                            bundle.putParcelable(TaxiEnroute.TaxiEnrouteData.ENROUTE_DATA, Parcels.wrap(data));
                            startState(States.TaxiEnroute, bundle);
                        } else {
                            Log.d(TAG, "Something wrong while fetching");
                        }
                    }
                });
                break;
            case EnrouteDest:
                final Location userLocation = user.getPickUpLocation();
                userLocation.fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Something went wrong!");
                        } else {
                            Bundle bundle = new Bundle();
                            EnrouteToDstState.EnrouteToDstData data = new EnrouteToDstState.EnrouteToDstData(
                                    new LatLng(userLocation.getLatitude(), userLocation.getLongitude()),
                                    user.getDriverId()
                            );
                            bundle.putParcelable(EnrouteToDstState.EnrouteToDstData.ENROUTE_DATA, Parcels.wrap(data));
                            startState(States.DestEnroute, bundle);
                        }
                    }
                });
                break;
            case DstReached:
                startState(States.DestArrived, null);
                break;
            default:
                throw new UnsupportedOperationException("Can't handle statee = " + state);
        }
    }

    public void startState(States state, Bundle data) {

        if (mCurrentState != null && state == mCurrentState.getState()) {
            return;
        }

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
            case DestArrived:
                mCurrentState = DestReached.getInstance();
                break;
            default:
                throw new UnsupportedOperationException("No such state");
        }
        mCurrentState.enterState(mAcitivity, data);
    }
}
