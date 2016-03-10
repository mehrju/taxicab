package com.xc0ffeelabs.taxicab.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.network.AccountManager;

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

    private void initializeParse() {

        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_URL).build());
    }

    public SecurePreferences getSecureSharedPreferences() {
        if (mSecurePrefs == null){
            mSecurePrefs = new SecurePreferences(this, "", "my_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }
}
