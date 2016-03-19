package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xc0ffeelabs.taxicab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlsFragment extends Fragment {

    public interface ControlsInteraction {
        void onPickupButtonClicked();
    }
    private ControlsInteraction mListener;

    private static ControlsFragment mControlsFragment = null;

    @Bind(R.id.btn_pickup) Button mPickupBtn;
    @Bind(R.id.tv_apprTime) TextView mApprTimeText;

    public static ControlsFragment getInstance() {
        if (mControlsFragment == null) {
            mControlsFragment= new ControlsFragment();
        }
        return mControlsFragment;
    }

    public void setControlsInteraction(ControlsInteraction listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls, container, false);

        ButterKnife.bind(this, view);

        mPickupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPickupButtonClicked();
                }
            }
        });

        setApprTime("--");
        return view;
    }

    public void setApprTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            String formattedStr = getContext().getString(R.string.appr_time, time);
            mApprTimeText.setText(formattedStr);
        }
    }

    public void setPickupEnabled(boolean enabled) {
        if (enabled) {
            mPickupBtn.setEnabled(true);
        } else {
            mPickupBtn.setEnabled(false);
        }
    }
}
