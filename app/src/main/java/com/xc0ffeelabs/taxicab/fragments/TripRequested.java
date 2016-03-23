package com.xc0ffeelabs.taxicab.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.xc0ffeelabs.taxicab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripRequested extends Fragment {

    @Bind(R.id.car_pb) View mCarPb;

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

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyAnimation();
    }

    public void applyAnimation() {
        AnimatorSet set1 = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mCarPb, "scaleX", 0.5f, 1.8f);
        scaleX.setDuration(1000);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new BounceInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mCarPb, "scaleY", 0.5f, 1.8f);
        scaleY.setDuration(1000);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new BounceInterpolator());

        set1.playTogether(scaleX, scaleY);
        set1.start();
    }

}
