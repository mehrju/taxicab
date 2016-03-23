package com.xc0ffeelabs.taxicab.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControlsFragment extends Fragment {

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String TAG = ControlsFragment.class.getSimpleName();

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

        mDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlacesAutoComplete();
            }
        });

        setApprTime("--", false);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mDest.setText(place.getName());
                LatLng destLocation = place.getLatLng();
                User user = (User) ParseUser.getCurrentUser();
                user.setDestLocation(destLocation);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Result cancelled");
            }
        }
    }

    private void launchPlacesAutoComplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "exception e = " + e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Exception e = " + e);
        }
    }

    public void setApprTime(String time, boolean raw) {
        if (!raw && !TextUtils.isEmpty(time) && getContext() != null) {
            String formattedStr = getContext().getString(R.string.appr_time, time);
            mApprTimeText.setText(formattedStr);
        } else {
            mApprTimeText.setText("No nearby drivers found");
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
        ObjectAnimator textAnim = ObjectAnimator.ofFloat(mApprTimeText, "Y",
                mApprTimeText.getBottom(), mApprTimeText.getTop());
        set1.playTogether(transition, fade, textAnim);

        AnimatorSet set2 = new AnimatorSet();
        ObjectAnimator fadeFab = ObjectAnimator.ofFloat(mPickupBtn, "alpha", 0f, 1f);
        ObjectAnimator fabScaleX = ObjectAnimator.ofFloat(mPickupBtn, "scaleX", 0f, 1f);
        ObjectAnimator fabScaleY = ObjectAnimator.ofFloat(mPickupBtn, "scaleY", 0f, 1f);
        set2.playTogether(fadeFab, fabScaleX, fabScaleY);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(set1, set2);

        set.start();
    }

    public void animateHideControls() {
        AnimatorSet set1 = new AnimatorSet();
        ObjectAnimator transition = ObjectAnimator.ofFloat(mTopControls, "y", 0, -mTopControls.getHeight());
        ObjectAnimator fade = ObjectAnimator.ofFloat(mTopControls, "alpha", 1f, 0f);
        set1.playTogether(transition, fade);

        AnimatorSet set2 = new AnimatorSet();
        ObjectAnimator fadeFab = ObjectAnimator.ofFloat(mPickupBtn, "alpha", 1f, 0f);
        ObjectAnimator fabScaleX = ObjectAnimator.ofFloat(mPickupBtn, "scaleX", 1f, 0f);
        ObjectAnimator fabScaleY = ObjectAnimator.ofFloat(mPickupBtn, "scaleY", 1f, 0f);
        ObjectAnimator textAnim = ObjectAnimator.ofFloat(mApprTimeText, "Y",
                mApprTimeText.getTop(), mApprTimeText.getBottom());
        set2.playTogether(fadeFab, fabScaleX, fabScaleY, textAnim);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(set2, set1);

        set.start();
    }

    public void setSourceLocation(String source) {
        mSource.setText(source);
    }

    public void setDestLocation(String dest) {
        mDest.setText(dest);
    }
}
