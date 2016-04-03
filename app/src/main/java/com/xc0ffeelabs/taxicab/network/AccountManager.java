package com.xc0ffeelabs.taxicab.network;

import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.securepreferences.SecurePreferences;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.models.User;

public class AccountManager {

    public interface LoginStatusCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    private static final String USERNAME = "user-name";
    private static final String PASSWORD = "passwd";
    private static final String FB_LOGIN = "fbLogin";
    private static final String ACCESS_TOKEN = "token";

    private static AccountManager mManager;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        if (mManager == null) {
            mManager = new AccountManager();
        }
        return mManager;
    }

    public boolean isCredentialsStored() {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        boolean isFbLogin = prefs.getBoolean(FB_LOGIN, false);
        if (isFbLogin) {
            return true;
        } else {
            String username = prefs.getString(USERNAME, "");
            String passwd = prefs.getString(PASSWORD, "");
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
                return false;
            }
            return true;
        }
    }

    public void storeCredentials(String username, String passwd) {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        SecurePreferences.Editor editor = prefs.edit();
        editor.putBoolean(FB_LOGIN, false);
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, passwd);
        editor.apply();
    }

    public void storeFbCredentials() {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        SecurePreferences.Editor editor = prefs.edit();
        editor.putBoolean(FB_LOGIN, true);
        editor.apply();
    }

    public void autoLoginUser(final LoginStatusCallback cb) {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        boolean isFbLogin = prefs.getBoolean(FB_LOGIN, false);
        if (!isFbLogin) {
            String username = prefs.getString(USERNAME, "");
            String passwd = prefs.getString(PASSWORD, "");
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
                cb.onLoginFailed();
            } else {
                loginUser(username, passwd, cb);
            }
        } else {
            fbLoginUser(cb);
        }
    }

    private void fbLoginUser(final LoginStatusCallback cb) {
        ParseFacebookUtils.logInInBackground(AccessToken.getCurrentAccessToken(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    User user1 = (User) user;
                    if (user1.getIsUserVerified())
                        cb.onLoginSuccess();
                    else
                        cb.onLoginFailed();
                } else {
                    Log.e("NAYAN", "Login failed. e = " + e.getMessage());
                    cb.onLoginFailed();
                }
            }
        });
    }

    /* TODO: Do we need to pass Exception details to clients? */
    public void loginUser(final String userName, final String passWd, final LoginStatusCallback callback) {
        ParseUser.logInInBackground(userName, passWd, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    if (!user.get("role").equals("user")) {
                        callback.onLoginFailed();
                    } else {
                        storeCredentials(userName, passWd);
                        callback.onLoginSuccess();
                    }
                } else {
                    callback.onLoginFailed();
                }
            }
        });
    }

    public void logoutUser() {
        ParseUser.logOut();
        storeCredentials("","");
    }
}
