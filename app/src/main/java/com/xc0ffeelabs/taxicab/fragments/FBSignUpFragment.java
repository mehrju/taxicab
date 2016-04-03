package com.xc0ffeelabs.taxicab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.AccountManager;
import com.xc0ffeelabs.taxicab.utilities.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FBSignUpFragment extends Fragment {

    @Bind(R.id.user_name) EditText mUserName;
    @Bind(R.id.user_phone) EditText mUserPhone;
    @Bind(R.id.user_email) EditText mUserEmail;
    @Bind(R.id.btn_signup) Button mCreateAcc;

    public interface FBSignUpInteraction {
        void startFbSignUp();
        void fbSignUpSuccess();
        void fbSignUpFailed();
    }

    private static FBSignUpFragment mFbSignup = null;

    private FBSignUpInteraction mSignUpInteraction;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignUpInteraction.startFbSignUp();

        User user = (User) ParseUser.getCurrentUser();
        mUserEmail.setText(user.getUsername());
        mUserName.setText(user.getName());
        mCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name = String.valueOf(mUserName.getText());
        String email = String.valueOf(mUserEmail.getText());
        String phNumber = String.valueOf(mUserPhone.getText());

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(getActivity(), R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty() || name.isEmpty() || phNumber.isEmpty()) {
            Toast.makeText(getActivity(), R.string.required_fields, Toast.LENGTH_LONG).show();
            return;
        }

        String phoneNumber = phNumber;
        if (!phoneNumber.startsWith("+1") && !phoneNumber.startsWith("1"))
            phoneNumber = "+1" + phoneNumber;
        else if (phoneNumber.startsWith("1"))
            phoneNumber = "+" + phoneNumber;

        User user = (User) ParseUser.getCurrentUser();
        user.setPhone(phoneNumber);
        user.setIsUserVerified(true);
        user.saveInBackground();

        storeCredentials();

        mSignUpInteraction.fbSignUpSuccess();
    }

    private void storeCredentials() {
        AccountManager.getInstance().storeFbCredentials();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSignUpInteraction = (FBSignUpInteraction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
