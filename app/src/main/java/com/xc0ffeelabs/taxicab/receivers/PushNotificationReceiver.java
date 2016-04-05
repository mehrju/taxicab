package com.xc0ffeelabs.taxicab.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.xc0ffeelabs.taxicab.activities.MapsActivity;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.services.CreateNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class PushNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";
    public static final String REQUEST_LAUNCH_MAP = "com.xc0ffeelabs.taxicab.LAUNCH_MAP";
    public static final String INTENT_TO_LAUNCH_MAP = "com.xc0ffeelabs.taxicabdriver.INTENT_TO_LAUNCH_MAP";
    public static final String INTENT_TO_LAUNCH_POPUP = "com.xc0ffeelabs.taxicabdriver.INTENT_TO_LAUNCH_POPUP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    String value = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + value);
                    JSONObject valueJson = new JSONObject(value);
                    // Extract custom push data
                    if (key.equals("customdata")) {
                        // create a local notification
                        setupTripState(context, valueJson, value);
                    } else if (key.equals("launch")) {
                        // Handle push notification by invoking activity directly
                        launchSomeActivity(context, value);
                    } else if (key.equals("broadcast")) {
                        // OR trigger a broadcast to activity
                        triggerBroadcastToActivity(context, value);
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    private void setupTripState(final Context context, final JSONObject valueJson, final String value) {
        try {
            String type = valueJson.getString("type");
            if (TextUtils.isEmpty(type)) {
                Log.e(TAG, "Type is not set. Return");
                return;
            }
            User.UserStates state;
            if (type.equalsIgnoreCase("taxiArrived")) {
                state = User.UserStates.EnrouteDest;
            } else {
                state = User.UserStates.DstReached;
            }
            User user = (User) ParseUser.getCurrentUser();
            user.setUserState(state);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    createNotification(context, value);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void createNotification(Context context, JSONObject valueJson) {
//        try {
//            Intent notificationIntent = new Intent(INTENT_TO_LAUNCH_MAP);
//
//            PendingIntent contentIntent = PendingIntent.getBroadcast(context,
//                    (int) System.currentTimeMillis(),
//                    notificationIntent,
//                    Intent.FILL_IN_DATA);
//
//            NotificationManager nm = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//            builder.setContentIntent(contentIntent)
//                    .setSmallIcon(R.drawable.ic_pin_black_18dp)
//                    .setWhen(System.currentTimeMillis())
//                    .setAutoCancel(true)
//                    .setContentTitle(valueJson.getString("title"))
//                    .setContentText(valueJson.getString("text"));
//
//            Notification n = builder.build();
//
//            n.defaults |= Notification.DEFAULT_ALL;
//            nm.notify(0, n);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications
    private void createNotification(Context context, String datavalue){

        try {

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> services = activityManager
                    .getRunningTasks(Integer.MAX_VALUE);
            JSONObject valJson = new JSONObject(datavalue);

            if (services.get(0).topActivity.getPackageName().toString()
                    .equalsIgnoreCase(context.getPackageName().toString())) {
                //App is active. Launch alert message
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
                Intent intent = new Intent(PushNotificationReceiver.REQUEST_LAUNCH_MAP);
                intent.putExtra("notiftype", "popup");
                intent.putExtra("actionType", valJson.getString("type"));
                intent.putExtra("tripId", valJson.getString("tripId"));
                intent.putExtra("tripUserId", valJson.getString("userId"));
                intent.putExtra("driverId", valJson.getString("driverId"));
                broadcastManager.sendBroadcast(intent);
                return;
            }

            //App is not active, create notification
            Intent it = new Intent(context, CreateNotification.class);
            it.putExtra("dataValue", datavalue);
            context.startService(it);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void launchSomeActivity(Context context, String datavalue) {
        Intent pupInt = new Intent(context, MapsActivity.class);
        pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pupInt.putExtra("data", datavalue);
        context.getApplicationContext().startActivity(pupInt);
    }

    // Handle push notification by sending a local broadcast
    // to which the activity subscribes to
    // See: http://guides.codepath.com/android/Starting-Background-Services#communicating-with-a-broadcastreceiver
    private void triggerBroadcastToActivity(Context context, String datavalue) {
        Intent intent = new Intent(intentAction);
        intent.putExtra("data", datavalue);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
