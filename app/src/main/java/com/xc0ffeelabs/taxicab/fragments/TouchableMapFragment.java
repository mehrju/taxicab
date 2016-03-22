package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class TouchableMapFragment extends SupportMapFragment {
    public View mOriginalContentView;
    public TouchableWrapper mTouchView;

    private OnTouchEvent mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.setOnTouchEvent(new TouchableWrapper.OnTouchEvent() {
            @Override
            public void onMapTouchDown() {
                mListener.onMapTouchDown();
            }

            @Override
            public void onMapTouchUp() {
                mListener.onMapTouchUp();
            }
        });
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    public interface OnTouchEvent {
        void onMapTouchDown();
        void onMapTouchUp();
    }

    public void setOnTouchListener(OnTouchEvent listener) {
        mListener = listener;
    }
}
