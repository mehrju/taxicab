package com.xc0ffeelabs.taxicab.states;

import android.os.Bundle;

import com.xc0ffeelabs.taxicab.activities.MapsActivity;

public interface State {
    void enterState(MapsActivity activity, Bundle data);
    void exitState();
    StateManager.States getState();
    void onTouchUp();
    void onTouchDown();
}
