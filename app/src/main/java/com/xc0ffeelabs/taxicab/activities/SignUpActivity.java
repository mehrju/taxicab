package com.xc0ffeelabs.taxicab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.utilities.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import xc0ffee.taxicab.R;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.user_name) EditText mUsername;
    @Bind(R.id.user_phone) EditText mPhoneNumber;
    @Bind(R.id.user_email) EditText mEmailAdd;
    @Bind(R.id.user_passwd) EditText mPasswd;
    @Bind(R.id.user_conf_password) EditText mConfPasswd;
    @Bind(R.id.btn_signup) Button mBtnSignUp;
    @Bind(R.id.pb_loading) View mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClicked();
            }
        });
    }

    private void onSignUpClicked() {
        final String email = String.valueOf(mEmailAdd.getText());
        final String password = String.valueOf(mPasswd.getText());
        String confirmPassword = String.valueOf(mConfPasswd.getText());
        String name = String.valueOf(mUsername.getText());
        String phNumber = String.valueOf(mPhoneNumber.getText());

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(this, R.string.email_invalid, Toast.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || name.isEmpty() || phNumber.isEmpty()) {
            Toast.makeText(this, R.string.required_fields, Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwd_mismatch, Toast.LENGTH_LONG).show();
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
        user.setRole();
        user.setPhone(phoneNumber);
        setLoading(true);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                setLoading(false);
                if (e == null) {
                    TaxiCabApplication.getAccountManager().storeCredentials(email, password);
                    signUpSuccess();
                } else {
                    Log.d(TAG, "Failed to sign up user");
                    Toast.makeText(SignUpActivity.this, "Unable to sign up user", Toast.LENGTH_SHORT).show();
                    signUpFailed();
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

    private void signUpSuccess() {
        Log.d(TAG, "User signed up!");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUpFailed() {

    }
}
