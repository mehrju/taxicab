package com.xc0ffeelabs.taxicab.activities;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicab.models.Location;
import com.xc0ffeelabs.taxicab.models.Trip;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.AccountManager;
import com.xc0ffeelabs.taxicab.network.NearbyDrivers;
import com.xc0ffeelabs.taxicab.states.StateManager;

public class TaxiCabApplication extends Application {

    private static final String APP_ID = "gotaxi";
    private static final String PARSE_URL = "https://gotaxi.herokuapp.com/parse/";

    private static TaxiCabApplication mApp;

    private SecurePreferences mSecurePrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initializeParse();
    }

    public static TaxiCabApplication get(){
        return mApp;
    }

    public static AccountManager getAccountManager() {
        return AccountManager.getInstance();
    }

    public static NearbyDrivers getNearbyDrivers() {
        return NearbyDrivers.get();
    }

    public static StateManager getStateManager() {
        return StateManager.getInstance();
    }

    private void initializeParse() {

        ParseObject.registerSubclass(Location.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Trip.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_URL).build());

        ParseFacebookUtils.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public SecurePreferences getSecureSharedPreferences() {
        if (mSecurePrefs == null){
            mSecurePrefs = new SecurePreferences(this, "", "my_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }

    public Context getAppContext() {
        return getApplicationContext();
    }
}
