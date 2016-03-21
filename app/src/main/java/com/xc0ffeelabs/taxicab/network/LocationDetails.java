package com.xc0ffeelabs.taxicab.network;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationDetails {
    public static Address getLocationDetails(Context context, LatLng location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses  == null || addresses.isEmpty()) {
                return null;
            }
            return addresses.get(0);
        } catch (IOException e) {
            return null;
        }
    }
}
