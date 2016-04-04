package com.xc0ffeelabs.taxicab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.models.FBResponse;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.utilities.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpFragment extends Fragment {

    public interface SignUpInteraction {
        void onSignUpSuccess();
        void onSignUpFailed();
    }

    private static final String TAG = SignUpFragment.class.getSimpleName();

    @Bind(R.id.user_name) EditText mUsername;
    @Bind(R.id.user_phone) EditText mPhoneNumber;
    @Bind(R.id.user_email) EditText mEmailAdd;
    @Bind(R.id.user_passwd) EditText mPasswd;
    @Bind(R.id.btn_signup) Button mBtnSignUp;
    @Bind(R.id.pb_loading) View mPbLoading;
    @Bind(R.id.fb_login) View mFbLogin;

    private static SignUpFragment mSignUpFragment = null;

    private SignUpInteraction mSignUpInteraction;

    public static SignUpFragment getInstance() {
        if (mSignUpFragment == null) {
            mSignUpFragment = new SignUpFragment();
        }
        return mSignUpFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSignUpInteraction = (SignUpInteraction) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClicked();
            }
        });

        mFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList();
                permissions.add("public_profile");
                permissions.add("email");
                setLoading(true);
                ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), permissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    setLoading(false);
                                    Log.d(TAG, "Uh oh. Error occurred" + err.toString());
                                    Toast.makeText(getActivity(), "Error while signing up", Toast.LENGTH_SHORT).show();
                                } else if (user == null) {
                                    setLoading(false);
                                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                    Toast.makeText(getActivity(), "Login cancelled", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "User logged in through Facebook!");
                                    getUserInfoFromFb();
                                }
                            }
                        });
            }
        });
    }

    private void getUserInfoFromFb() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        setLoading(false);
                        if (response == null) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        } else {
                            Gson gson = new GsonBuilder().create();
                            FBResponse fbResponse  = gson.fromJson(response.getJSONObject().toString(), FBResponse.class);
                            User user = (User) ParseUser.getCurrentUser();
                            user.setUsername(fbResponse.email);
                            user.setName(fbResponse.name);
                            user.setIsFbLogin(true);
                            user.setRole(User.USER_ROLE);
                            if (fbResponse.picture != null &&
                                    fbResponse.picture.data != null) {
                                user.setProfieImage(fbResponse.picture.data.url);
                            }
                            user.setIsUserVerified(false);
                            user.saveInBackground();
                            switchToVerifyFragment();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void switchToVerifyFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fm_placeholder, FBSignUpFragment.getInstance()).commit();
    }

    private void onSignUpClicked() {
        final String email = String.valueOf(mEmailAdd.getText());
        final String password = String.valueOf(mPasswd.getText());
        String name = String.valueOf(mUsername.getText());
        String phNumber = String.valueOf(mPhoneNumber.getText());

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(getActivity(), R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty()
                || name.isEmpty() || phNumber.isEmpty()) {
            Toast.makeText(getActivity(), R.string.required_fields, Toast.LENGTH_LONG).show();
            return;
        }

        String phoneNumber = mPhoneNumber.getText().toString();
        if (!phoneNumber.startsWith("+1") && !phoneNumber.startsWith("1"))
            phoneNumber = "+1" + phoneNumber;
        else if (phoneNumber.startsWith("1"))
            phoneNumber = "+" + phoneNumber;

        // Store in Parse
        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setName(name);
        user.setRole(User.USER_ROLE);
        user.setPhone(phoneNumber);
        user.saveInBackground();
        setLoading(true);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                setLoading(false);
                if (e == null) {
                    TaxiCabApplication.getAccountManager().storeCredentials(email, password);
                    mSignUpInteraction.onSignUpSuccess();
                } else {
                    Log.d(TAG, "Failed to sign up user");
                    Toast.makeText(getActivity(), "Unable to sign up user", Toast.LENGTH_SHORT).show();
                    mSignUpInteraction.onSignUpFailed();
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mPbLoading.setVisibility(View.VISIBLE);
        } else {
            mPbLoading.setVisibility(View.GONE);
        }
    }
}
