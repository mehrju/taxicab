package xc0ffee.taxicab.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

import xc0ffee.taxicab.models.User;
import xc0ffee.taxicab.network.AccountManager;

public class TaxiCabApplication extends Application {

    private static final String APP_ID = "gotaxi";
    private static final String PARSE_URL = "https://gotaxi.herokuapp.com/parse/";

    @Override
    public void onCreate() {
        super.onCreate();

        initializeParse();
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
}
