package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xc0ffeelabs.taxicab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlsFragment extends Fragment {

    private static ControlsFragment mControlsFragment = null;

    @Bind(R.id.tv_apprTime) TextView mApprTimeText;

    public static ControlsFragment newInstance() {
        if (mControlsFragment == null) {
            mControlsFragment= new ControlsFragment();
        }
        return mControlsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls, container, false);

        ButterKnife.bind(this, view);
        setApprTime("--");
        return view;
    }

    public void setApprTime(String time) {
        String formattedStr = getContext().getString(R.string.appr_time, time);
        mApprTimeText.setText(formattedStr);
    }
}
