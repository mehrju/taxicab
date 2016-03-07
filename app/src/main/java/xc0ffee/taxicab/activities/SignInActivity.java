package xc0ffee.taxicab.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import xc0ffee.taxicab.R;
import xc0ffee.taxicab.utilities.Utils;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    @Bind(R.id.user_email) EditText mUserEmail;
    @Bind(R.id.user_passwd) EditText mUserPasswd;
    @Bind(R.id.btn_signin) Button mBtnSignIn;
    @Bind(R.id.btn_signup) Button mBtnSignup;
    @Bind(R.id.pb_loading) View mPbLoading;
    @Bind(R.id.toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInBtnClicked();
            }
        });
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
        ParseUser.logInInBackground(name, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                setLoading(false);
                if (e == null) {
                    Log.d(TAG, "User logged in");
                } else {
                    Log.d(TAG, "Failed to login user");
                    Toast.makeText(SignInActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
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
}
