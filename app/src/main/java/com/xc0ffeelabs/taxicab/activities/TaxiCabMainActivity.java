package com.xc0ffeelabs.taxicab.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.facebook.appevents.AppEventsLogger;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.network.AccountManager;
import com.xc0ffeelabs.taxicab.utilities.NetworkUtils;
import com.xc0ffeelabs.taxicab.utilities.Utils;

public class TaxiCabMainActivity extends AppCompatActivity {

    private static final int NO_PERM_ACTIVITY_LAUNCH_CODE = 100;

    private boolean mIsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_cab_main);
        if (Utils.isM()) {
            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, NoPermissionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                initialize();
            }
        } else {
            initialize();
        }
    }

    private void initialize() {
        mIsInitialized = true;
        new CheckOnlineStatus(this).execute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isM() && !mIsInitialized) {
            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                initialize();
            }
        }
        AppEventsLogger.activateApp(this);
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showNoNetworkMessage() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.no_network_title))
                .setMessage(getString(R.string.no_network_msg))
                .setPositiveButton(getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private class DetermineLoginStatus extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return TaxiCabApplication.getAccountManager().isCredentialsStored();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                TaxiCabApplication.getAccountManager().autoLoginUser(
                        new AccountManager.LoginStatusCallback() {
                            @Override
                            public void onLoginSuccess() {
                                loggedIn();
                            }

                            @Override
                            public void onLoginFailed() {
                                loginFailed();
                            }
                        }
                );
            } else {
                showLoginActivity();
            }
        }
    }

    private class CheckOnlineStatus extends AsyncTask<Void, Void, Boolean> {

        final private Context mContext;

        public CheckOnlineStatus(Context context) {
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return NetworkUtils.isNetworkAvailable(mContext) && NetworkUtils.isOnline();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                new DetermineLoginStatus().execute();
            } else {
                showNoNetworkMessage();
            }
        }
    }

    private void loggedIn() {
        // Launch map activity here
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void loginFailed() {
        showLoginActivity();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_PERM_ACTIVITY_LAUNCH_CODE) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            } else {
                new CheckOnlineStatus(this).execute();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
