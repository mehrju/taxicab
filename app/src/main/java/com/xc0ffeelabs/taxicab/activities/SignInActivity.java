package com.xc0ffeelabs.taxicab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.AccountManager;
import com.xc0ffeelabs.taxicab.utilities.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    @Bind(R.id.user_email) EditText mUserEmail;
    @Bind(R.id.user_passwd) EditText mUserPasswd;
    @Bind(R.id.btn_signin) Button mBtnSignIn;
    @Bind(R.id.btn_signup) Button mBtnSignup;
    @Bind(R.id.pb_loading) View mPbLoading;
    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.fb_login) View mFbSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mToolBar.setNavigationIcon(R.drawable.ic_chariot_logo_9);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInBtnClicked();
            }
        });

        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpBtnClicked();
            }
        });

        mFbSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> permissions = new ArrayList();
                permissions.add("email");
                setLoading(true);
                ParseFacebookUtils.logInWithReadPermissionsInBackground(SignInActivity.this, permissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                setLoading(false);
                                if (err != null) {
                                    Log.d(TAG, "Uh oh. Error occurred" + err.toString());
                                } else if (user == null) {
                                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                } else {
                                    Log.d(TAG, "User logged in through Facebook!");
                                    checkLogin();
                                }
                            }
                        });
            }
        });
    }

    private void checkLogin() {
        User user = (User) ParseUser.getCurrentUser();
        if (user.getIsUserVerified()) {
            AccountManager.getInstance().storeFbCredentials();
            signInSuccess();
        } else {
            Toast.makeText(this, "Please signup first", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSignUpBtnClicked() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void onSignInBtnClicked() {
        final String email = mUserEmail.getText().toString();

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        final String password = mUserPasswd.getText().toString();
        login(email, password);
    }

    private void login(String name, String password) {
        setLoading(true);
        TaxiCabApplication.getAccountManager().loginUser(name, password, new AccountManager.LoginStatusCallback() {
            @Override
            public void onLoginSuccess() {
                setLoading(false);
                Log.d(TAG, "User logged in");
                signInSuccess();
            }

            @Override
            public void onLoginFailed() {
                setLoading(false);
                Log.d(TAG, "Failed to login user");
                Toast.makeText(SignInActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
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

    private float getDeviceWidth() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return  displayMetrics.widthPixels ;
    }


    private void signInSuccess() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
