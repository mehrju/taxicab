package com.xc0ffeelabs.taxicab.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.fragments.MapsFragment;
import com.xc0ffeelabs.taxicab.fragments.NotificationDialog;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.receivers.PushNotificationReceiver;
import com.xc0ffeelabs.taxicab.states.StateManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements MapsFragment.MapReady{

    @Bind(R.id.nvView) NavigationView mNavView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.dl_nav_drawer) DrawerLayout mDrawer;
    @Bind(R.id.fm_controls) View mControls;
    @Bind(R.id.progress_bar) View mPb;

    private ActionBarDrawerToggle mDrawerToggle;
    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private Drawable mLogo;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("notiftype");
            if ("popup".equals(type)) {
                //show pupup message
                showRequestNotificationFragment(intent);
            } else {
                TaxiCabApplication.getStateManager().startDefaultState();
                Intent mapsIntent = new Intent(context, MapsActivity.class);
                mapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(mapsIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mLogo = mToolbar.getLogo();

        setupNavDrawer();

        setupProfile();

        TaxiCabApplication.getStateManager().setActivity(this);

        registerUserWithParseInstallation();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance();
        mapsFragment.setMapReadyListener(this);
        ft.replace(R.id.fm_placeholder, mapsFragment);
        ft.commit();

        mapsFragment.setOnTouchListener(new MapsFragment.OnTouchEvent() {
            @Override
            public void onMapTouchDown() {
                StateManager.getInstance().getCurrentState().onTouchDown();
            }

            @Override
            public void onMapTouchUp() {
                StateManager.getInstance().getCurrentState().onTouchUp();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(PushNotificationReceiver.REQUEST_LAUNCH_MAP));
        setLoading(true);
    }

    public void setLoading(boolean loading) {
        if (loading) mPb.setVisibility(View.VISIBLE);
        else mPb.setVisibility(View.GONE);
    }

    private void registerUserWithParseInstallation() {
        User user = (User) ParseUser.getCurrentUser();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("ownerId", user.getObjectId());
        installation.saveInBackground();
    }

    private void setupProfile() {
        User user = (User)ParseUser.getCurrentUser();
        String imageUrl = user.getProfileImage();
        ImageView iv = (ImageView)mNavView.getHeaderView(0).findViewById(R.id.profileImage);
        if (imageUrl!=null) {
            Picasso.with(this).load(imageUrl).into(iv);
        }
        TextView name = (TextView)mNavView.getHeaderView(0).findViewById(R.id.UserName);
        name.setText(user.getName());

        TextView email = (TextView)mNavView.getHeaderView(0).findViewById(R.id.email);
        email.setText(user.getEmail());
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
            case R.id.history:
                launchHistory();
                break;
            case R.id.trip_estimate:
                launchFareEstimate();
                break;
            case R.id.profile:
                launchProfileView();
                break;
            case R.id.book_in_advance:
                launchAdvanceBooking();
                break;
            default:
                throw new UnsupportedOperationException("Invalid menu item clicked");
        }
        mDrawer.closeDrawers();
    }

    private void launchAdvanceBooking() {
        Intent intent = new Intent(this, BookInAdvanceActivity.class);
        startActivity(intent);
    }

    private void launchProfileView() {
            new AlertDialog.Builder(this).setTitle("Check back later.")
                    .setMessage("Profile view is not yet enabled. Stay tuned!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
    }

    private void launchFareEstimate() {
        new AlertDialog.Builder(this).setTitle("Check back later.")
                .setMessage("This functionality is not yet enabled. Stay tuned!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    private void launchHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
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

    private void showRequestNotificationFragment(Intent intent) {
        FragmentManager fm = getSupportFragmentManager();

        String tripId = intent.getStringExtra("tripId");
        String driverId = intent.getStringExtra("driverId");
        String actionType = intent.getStringExtra("actionType");
        NotificationDialog rideRequest = NotificationDialog.newInstance(driverId, tripId, actionType);
        rideRequest.show(fm, "fragment_edit_name");
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
        StateManager.getInstance().resetCurrentState();
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
        StateManager.getInstance().resetCurrentState();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setTitle(String title) {
        mToolbar.setTitle(title);
    }

    public void setIcon(int drawable) {
        if (drawable == -1) {
            mToolbar.setNavigationIcon(mLogo);
        } else {
            mToolbar.setNavigationIcon(drawable);
        }
    }
}
