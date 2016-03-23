package com.xc0ffeelabs.taxicab.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.squareup.picasso.Picasso;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.models.Trip;
import com.xc0ffeelabs.taxicab.utilities.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class TripHistoryAdapter extends RecyclerView.Adapter<TripHistoryAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    private List<Trip> mTrips;
    public TripHistoryAdapter(List<Trip> trips) {
        mTrips = trips;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView userImage;
        public TextView userName;
        public TextView tripDate;
        public TextView tripAmount;
        public TextView startLocation;
        public TextView destLocation;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            tripDate = (TextView) itemView.findViewById(R.id.tripDate);
            tripAmount = (TextView) itemView.findViewById(R.id.tripAmount);
            startLocation = (TextView) itemView.findViewById(R.id.startPoint);
            destLocation = (TextView) itemView.findViewById(R.id.endPoint);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.history_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Trip trip = (Trip)mTrips.get(position);

        // Set item views based on the data model
        try {
            holder.userName.setText(trip.getDriver() != null && trip.getDriver().getString("name") !=null ? trip.getDriver().getString("name"):"");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tripDate.setText(new SimpleDateFormat("EEE MMM d, h:mm a").format(trip.getCreatedAt()));

        String picUrl = "https://randomuser.me/api/portraits/med/men/"+ new Random().nextInt(20) + ".jpg"; // 1.jpg
        holder.tripAmount.setText("$" + new Random().nextInt(20));

        try {
            picUrl = trip.getDriver() != null && trip.getDriver().getString("profileImage") !=null && trip.getDriver().getString("profileImage").length() > 0 ? trip.getDriver().getString("profileImage"):picUrl;


            ImageView userImage = holder.userImage;
            Picasso.with(userImage.getContext()).load(picUrl).transform(new CircleTransform()).into(userImage);

            String source = trip.getString("pickUpLocationString") != null ? trip.getString("pickUpLocationString") : "1 Facebook Way, Menlo Park, CA";
            String dest = trip.getString("destLocationString") != null ? trip.getString("pickUpLocationString") : "Microsoft SVC Building 1, Mountain View, CA";
            holder.startLocation.setText(Html.fromHtml("Src: <i>"+ source + "</i>"));

            holder.destLocation.setText(Html.fromHtml("Dest: <i>"+ dest + "</i>"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public void addItems(List<Trip> items) {
        mTrips.addAll(items);
        this.notifyDataSetChanged();
    }
}