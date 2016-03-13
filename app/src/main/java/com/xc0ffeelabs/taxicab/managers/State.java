package com.xc0ffeelabs.taxicab.managers;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;

public interface State {
    void enterState(MapsActivity activity, GoogleMap map, GoogleApiClient client);
    void exitState();
}
