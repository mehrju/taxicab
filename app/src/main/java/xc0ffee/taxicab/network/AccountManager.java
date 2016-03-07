package xc0ffee.taxicab.network;

public class AccountManager {

    private static AccountManager mManager;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        if (mManager == null) {
            mManager = new AccountManager();
        }
        return mManager;
    }

    public boolean hasUserLoggedIn() {
        return false;
    }
}
