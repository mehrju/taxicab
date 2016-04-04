package com.xc0ffeelabs.taxicab.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicab.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaxiEnrouteFragment extends Fragment {

    @Bind(R.id.pbLoading) View mProgressBar;
    @Bind(R.id.tv_car_name) TextView mCarName;
    @Bind(R.id.tv_name) TextView mDriverName;
    @Bind(R.id.tv_profile) ImageView mProfile;
    @Bind(R.id.call) View mCall;

    private String mPhNumber;

    private static TaxiEnrouteFragment mEnrouteFragment;
    private boolean mHidePbRequested = false;

    public static TaxiEnrouteFragment getInstance() {
        if (mEnrouteFragment == null) {
            mEnrouteFragment = new TaxiEnrouteFragment();
        }

        return mEnrouteFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxi_enroute, container, false);

        ButterKnife.bind(this, view);

        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mPhNumber)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mPhNumber));
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mHidePbRequested) {
            mProgressBar.setVisibility(View.GONE);
            mHidePbRequested = false;
        }
    }

    public void hideProgress() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mHidePbRequested = true;
        }
    }

    public void setDriverName(String name) {
        mDriverName.setText(name);
    }

    public void setCarName(String name) {
        mCarName.setText(name);
    }

    public void setImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(getContext()).load(imageUrl).into(mProfile);
        }
    }

    public void setPhone(String phone) {
        mPhNumber = phone;
    }
}
