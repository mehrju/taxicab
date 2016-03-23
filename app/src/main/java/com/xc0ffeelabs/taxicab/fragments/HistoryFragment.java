package com.xc0ffeelabs.taxicab.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.adapter.TripHistoryAdapter;
import com.xc0ffeelabs.taxicab.models.Trip;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment {
    @Bind(R.id.rvHistory)
    RecyclerView historyView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    TripHistoryAdapter adapter;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

//        attachAdapter(view);

        adapter = new TripHistoryAdapter(new ArrayList<Trip>());
        historyView.setAdapter(adapter);
        historyView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        new HistoryDowloadTask().execute();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


//    private void attachAdapter(final View view) {
//        ParseUser user = ParseUser.getCurrentUser();
//
//        ParseQuery<Trip> trips = ParseQuery.getQuery("Trip");
//        trips.include("driver");
//        trips.whereEqualTo("user", user);
//        trips.findInBackground(new FindCallback() {
//            @Override
//            public void done(List objects, ParseException e) {
//                List<Trip> trips = objects;
//                TripHistoryAdapter adapter = new TripHistoryAdapter(trips);
//                historyView.setAdapter(adapter);
//                historyView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void done(Object o, Throwable throwable) {
//
//                List<Trip> result = (ArrayList<Trip>)o;
//                adapter.addItems(result);
//                adapter.notifyDataSetChanged();
//
//                historyView.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//
//        adapter = new TripHistoryAdapter(new ArrayList<Trip>());
//        historyView.setAdapter(adapter);
//        historyView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//    }


    // Defines the background task to download history
    private class HistoryDowloadTask extends AsyncTask<String, Void, List<Trip>> {
        List<Trip> tripHistory;

        public HistoryDowloadTask() {
        }

        protected List<Trip> doInBackground(String... addresses) {
            ParseUser user = ParseUser.getCurrentUser();
            ParseQuery<Trip> trips = ParseQuery.getQuery("Trip");
            trips.include("driver");
            trips.include("user");
            trips.whereEqualTo("user", user);
            try {
                tripHistory =  trips.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return tripHistory;
        }

        // Fires after the task is completed, displaying results
        @Override
        protected void onPostExecute(List<Trip> history) {
            // Set bitmap image for the result
            adapter.addItems(history);
            adapter.notifyDataSetChanged();
            historyView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

}
