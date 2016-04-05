package com.xc0ffeelabs.taxicab.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.fragments.DatePickerFragment;
import com.xc0ffeelabs.taxicab.fragments.TimePickerFragment;
import com.xc0ffeelabs.taxicab.models.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookInAdvanceActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "BookInAdvance";

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.et_date_picker) EditText mDatePicker;
    @Bind(R.id.et_date_time) EditText mTimePicker;
    @Bind(R.id.et_source) EditText mSource;
    @Bind(R.id.et_dst) EditText mDst;
    @Bind(R.id.btn_reserve) Button mReserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_in_advance);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        mSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlacesAutoCompleteSrc();
            }
        });

        mDst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlacesAutoCompleteDst();
            }
        });

        mReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BookInAdvanceActivity.this).setTitle("Confirmed!").
                        setMessage("Reservation confirmed. Enjoy your ride")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // attach to an onclick handler to show the date picker
    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        mDatePicker.setText(format1.format(c.getTime()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimePicker.setText(hourOfDay + ":" + minute);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mSource.setText(place.getName());
                LatLng destLocation = place.getLatLng();
                User user = (User) ParseUser.getCurrentUser();
                user.setDestLocation(destLocation, place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Result cancelled");
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mDst.setText(place.getName());
                LatLng destLocation = place.getLatLng();
                User user = (User) ParseUser.getCurrentUser();
                user.setDestLocation(destLocation, place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Result cancelled");
            }
        }
    }

    private void launchPlacesAutoCompleteSrc() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "exception e = " + e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Exception e = " + e);
        }
    }

    private void launchPlacesAutoCompleteDst() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "exception e = " + e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Exception e = " + e);
        }
    }
}
