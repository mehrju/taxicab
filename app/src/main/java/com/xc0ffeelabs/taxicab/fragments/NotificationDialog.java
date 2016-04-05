package com.xc0ffeelabs.taxicab.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.activities.TaxiCabApplication;
import com.xc0ffeelabs.taxicab.models.Location;
import com.xc0ffeelabs.taxicab.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skammila on 3/21/16.
 */
public class NotificationDialog extends DialogFragment {
    @Bind(R.id.notificationText)
    TextView notificationText;

    @Bind(R.id.frgTitle)
    TextView title;


    @Bind(R.id.profileImage)
    RoundedImageView profileImage;

    @Bind(R.id.okBtn)Button okBtn;


    User tripDriver;
    String tripId;
    String type;


    public NotificationDialog() {

    }

    public static NotificationDialog newInstance(String driverId, String tripId, String type) {
        NotificationDialog frag = new NotificationDialog();
        User driver = null;
        try {
            driver = (User)ParseUser.getQuery().get(driverId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        frag.setTripDriver(driver);
        frag.setTripId(tripId);
        frag.setType(type);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_request_notif, container);
        ButterKnife.bind(this, view);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (type.equals("taxiArrived")) {
//                    //taxi arrived
//                } else if (type.equals("destinationArrived")) {
//                    // destination reached
//
//                }
                onNotificationOk();
//                ((MapsActivity) getActivity()).onAccept(getTripId());
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type.equals("taxiArrived")) {
            //show taxi arrived popup
            createTaxiArrivedNotification();
        } else if (type.equals("destinationArrived")) {
            //show destination reached popup
            createDestReachedNotification();
        } else {
            Log.e("NotificationDialog", "Invalid notification type " + type);
        }

    }

    public void onNotificationOk() {
        TaxiCabApplication.getStateManager().startDefaultState();
        Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
        mapsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(mapsIntent);
    }

    private void createTaxiArrivedNotification() {
        String name = tripDriver != null && tripDriver.getName() != null ? tripDriver.getName() : "Taxi";

        name = firstLetterUppercase(name);

        StyleSpan bold1 = new StyleSpan(Typeface.BOLD);

        SpannableStringBuilder ssb = new SpannableStringBuilder(name);

        ssb.setSpan(bold1, 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append(" arrived to drive you to ");
        StyleSpan italic1 = new StyleSpan(Typeface.ITALIC);

        Location dest = ((User)ParseUser.getCurrentUser()).getDestLocation();
        try {
            if (dest != null)
                dest.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dest != null && dest.getText() != null && dest.getText().length() > 0) {
            ssb.append(dest.getText());
            ssb.setSpan(italic1, ssb.length() - dest.getText().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ssb.append("your destination");
        }
        String msg4 = ".\nLook for the taxi and board.";
        ssb.append(msg4);


        notificationText.setText(ssb);

        title.setText("Taxi Arrived");

        String profileImageUrl = tripDriver.getProfileImage();
        if (profileImageUrl != null && profileImageUrl.length()>0) {
            Picasso.with(getContext()).load(profileImageUrl).into(profileImage);
        }
    }

    private void createDestReachedNotification() {
        //notify, You have reached destination

        String msg = "You have reached ";

        SpannableStringBuilder ssb = new SpannableStringBuilder(msg);

        StyleSpan italic1 = new StyleSpan(Typeface.ITALIC);

        Location dest = ((User)ParseUser.getCurrentUser()).getDestLocation();
        try {
            if (dest != null)
                dest.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dest != null && dest.getText() != null && dest.getText().length() > 0) {
            ssb.append(dest.getText());
            ssb.setSpan(italic1, ssb.length() - dest.getText().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(". ");
        } else {
            ssb.append("destination. ");
        }

        ssb.append("Thanks for riding with Chariot.");

        notificationText.setText(ssb);

        title.setText("Reached Destination");

        String profileImageUrl = tripDriver.getProfileImage();
        if (profileImageUrl != null && profileImageUrl.length()>0) {
            Picasso.with(getContext()).load(profileImageUrl).into(profileImage);
        }
    }
    private String firstLetterUppercase(String inp) {
        StringBuilder rackingSystemSb = new StringBuilder(inp.toLowerCase());
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        return rackingSystemSb.toString();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public User getTripDriver() {
        return tripDriver;
    }

    public void setTripDriver(User tripDriver) {
        this.tripDriver = tripDriver;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
