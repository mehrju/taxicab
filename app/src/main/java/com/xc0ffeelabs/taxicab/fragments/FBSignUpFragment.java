package com.xc0ffeelabs.taxicab.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xc0ffeelabs.taxicab.R;

import butterknife.ButterKnife;

public class FBSignUpFragment extends Fragment {

    private static FBSignUpFragment mFbSignup = null;

    public static FBSignUpFragment getInstance() {
        if (mFbSignup == null) {
            mFbSignup = new FBSignUpFragment();
        }
        return mFbSignup;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fbsign_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
