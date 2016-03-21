package com.xc0ffeelabs.taxicab.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    @Bind(R.id.btn_pickup) View mPickupBtn;
    @Bind(R.id.tv_apprTime) TextView mApprTimeText;
    @Bind(R.id.et_source) EditText mSource;
    @Bind(R.id.et_dst) EditText mDest;
    @Bind(R.id.topControls) View mTopControls;

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
        if (!TextUtils.isEmpty(time) && getContext() != null) {
            String formattedStr = getContext().getString(R.string.appr_time, time);
            mApprTimeText.setText(formattedStr);
        }
    }

    public void setPickupEnabled(boolean enabled) {
        if (enabled) {
            mPickupBtn.setEnabled(true);
        } else {
            mPickupBtn.setEnabled(false);
            mApprTimeText.setText("--");
        }
    }

    public void animateShowControls() {
        AnimatorSet set1 = new AnimatorSet();
        ObjectAnimator transition = ObjectAnimator.ofFloat(mTopControls, "y", -mTopControls.getHeight(), 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(mTopControls, "alpha", 0f, 1f);
        set1.playTogether(transition, fade);

        AnimatorSet set2 = new AnimatorSet();
        ObjectAnimator fadeFab = ObjectAnimator.ofFloat(mPickupBtn, "alpha", 0f, 1f);
        ObjectAnimator fabScaleX = ObjectAnimator.ofFloat(mPickupBtn, "scaleX", 0f, 1f);
        ObjectAnimator fabScaleY = ObjectAnimator.ofFloat(mPickupBtn, "scaleU", 0f, 1f);
        set2.playTogether(fadeFab, fabScaleX, fabScaleY);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(set1, set2);

        set.start();
    }

    public void setSourceLocation(String source) {
        mSource.setText(source);
    }

    public void setDestLocation(String dest) {
        mDest.setText(dest);
    }
}
