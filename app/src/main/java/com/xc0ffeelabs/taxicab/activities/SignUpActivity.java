package com.xc0ffeelabs.taxicab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.fragments.SignUpFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements SignUpFragment.SignUpInteraction {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_chariot_logo_9);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fm_placeholder, SignUpFragment.getInstance()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onSignUpSuccess() {
        Log.d(TAG, "User signed up!");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onSignUpFailed() {

    }
}
