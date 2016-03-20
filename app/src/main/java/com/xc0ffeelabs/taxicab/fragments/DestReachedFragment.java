package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xc0ffeelabs.taxicab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DestReachedFragment extends Fragment {

    @Bind(R.id.btn_endtrip) Button mBtnEndTrip;

    public interface TripEndInteraction {
        void onTripEnded();
    }

    private static DestReachedFragment mDstReached;

    private TripEndInteraction mListener;

    public static DestReachedFragment getInstance() {
        if (mDstReached == null) {
            mDstReached = new DestReachedFragment();
        }
        return mDstReached;
    }

    public void setTripEndInteraction(TripEndInteraction listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dst_reached, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mBtnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTripEnded();
                }
            }
        });
    }
}
