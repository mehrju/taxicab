package xc0ffee.taxicab.network;

import android.text.TextUtils;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.securepreferences.SecurePreferences;

import xc0ffee.taxicab.activities.TaxiCabApplication;

public class AccountManager {

    public interface LoginStatusCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }

    private static final String USERNAME = "user-name";
    private static final String PASSWORD = "passwd";

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
        String username = prefs.getString(USERNAME, "");
        String passwd = prefs.getString(PASSWORD, "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
            return false;
        }
        return true;
    }

    public void storeCredentials(String username, String passwd) {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        SecurePreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, passwd);
        editor.apply();
    }

    public void autoLoginUser(final LoginStatusCallback cb) {
        SecurePreferences prefs = TaxiCabApplication.get().getSecureSharedPreferences();
        String username = prefs.getString(USERNAME, "");
        String passwd = prefs.getString(PASSWORD, "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwd)) {
            cb.onLoginFailed();
        } else {
            loginUser(username, passwd, cb);
        }
    }

    /* TODO: Do we need to pass Exception details to clients? */
    public void loginUser(final String userName, final String passWd, final LoginStatusCallback callback) {
        ParseUser.logInInBackground(userName, passWd, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    storeCredentials(userName, passWd);
                    callback.onLoginSuccess();
                } else {
                    callback.onLoginFailed();
                }
            }
        });
    }
}
