package com.xc0ffeelabs.taxicab.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.fragments.MapsFragment;
import com.xc0ffeelabs.taxicab.managers.StateManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity {

    @Bind(R.id.nvView) NavigationView mNavView;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.dl_nav_drawer) DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setupNavDrawer();

        TaxiCabApplication.getStateManager().setActivity(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = MapsFragment.newInstance();
        mapsFragment.setMapReadyListener(TaxiCabApplication.getStateManager());
        ft.replace(R.id.fm_placeholder, mapsFragment);
        ft.commit();

        TaxiCabApplication.getStateManager().startState(StateManager.States.ListDriver);
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
        finish();
    }
}
