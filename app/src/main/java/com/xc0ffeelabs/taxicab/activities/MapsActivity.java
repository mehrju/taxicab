package com.xc0ffeelabs.taxicab.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.fragments.MapsFragment;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.receivers.PushNotificationReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements MapsFragment.MapReady{

    @Bind(R.id.nvView) NavigationView mNavView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.dl_nav_drawer) DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NAYAN", "On Receive of broadcast");
            Intent mapsIntent = new Intent(context, MapsActivity.class);
            mapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mapsIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setupNavDrawer();

        TaxiCabApplication.getStateManager().setActivity(this);

        registerUserWithParseInstallation();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance();
        mapsFragment.setMapReadyListener(this);
        ft.replace(R.id.fm_placeholder, mapsFragment);
        ft.commit();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(PushNotificationReceiver.REQUEST_LAUNCH_MAP));
    }

    private void registerUserWithParseInstallation() {
        User user = (User) ParseUser.getCurrentUser();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("ownerId", user.getObjectId());
        installation.saveInBackground();
    }

    private void setupNavDrawer() {
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
    }

    private void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nd_logout:
                logoutUserConf();
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu item clicked");
        }
        mDrawer.closeDrawers();
    }

    private void logoutUserConf() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout_conf)
                .setMessage(R.string.logout_msg)
                .setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        TaxiCabApplication.getAccountManager().logoutUser();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map, GoogleApiClient apiClient) {
        mMap = map;
        mApiClient = apiClient;
        TaxiCabApplication.getStateManager().startDefaultState();
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
