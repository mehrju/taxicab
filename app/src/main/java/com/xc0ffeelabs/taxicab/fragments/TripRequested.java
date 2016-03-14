package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xc0ffeelabs.taxicab.R;

public class TripRequested extends Fragment {

    private static TripRequested mTripRequested;

    public static Fragment newInstance() {
        if (mTripRequested == null) {
            mTripRequested = new TripRequested();
        }
        return mTripRequested;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_requested, container, false);
        return view;
    }
}
