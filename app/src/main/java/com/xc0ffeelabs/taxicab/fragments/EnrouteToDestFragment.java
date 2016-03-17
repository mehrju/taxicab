package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xc0ffeelabs.taxicab.R;

import butterknife.ButterKnife;

public class EnrouteToDestFragment extends Fragment {

    private static EnrouteToDestFragment mEnrouteDst;

    public static EnrouteToDestFragment getInstance() {
        if (mEnrouteDst == null) {
            mEnrouteDst = new EnrouteToDestFragment();
        }

        return mEnrouteDst;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxi_dst, container, false);

        ButterKnife.bind(this, view);

        return view;
    }
}
