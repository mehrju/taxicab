package com.xc0ffeelabs.taxicab.managers;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

public interface State {
    void enterState(GoogleMap map, GoogleApiClient client);
    void exitState();
}
