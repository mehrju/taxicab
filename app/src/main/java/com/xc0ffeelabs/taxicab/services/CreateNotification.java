package com.xc0ffeelabs.taxicab.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.xc0ffeelabs.taxicab.R;
import com.xc0ffeelabs.taxicab.models.Location;
import com.xc0ffeelabs.taxicab.models.User;
import com.xc0ffeelabs.taxicab.receivers.PushNotificationReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by skammila on 4/2/16.
 */
public class CreateNotification extends IntentService {
    private String text;
    private String userId;
    private String driverId;
    private String tripId;
    private String type;
    private String title;
    public CreateNotification() {
        super("CreateNotification");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String dataValue = intent.getStringExtra("dataValue");
        JSONObject dataJson = null;
        try {
            dataJson = new JSONObject(dataValue);
            userId = dataJson.getString("userId");
            driverId = dataJson.getString("driverId");
            tripId = dataJson.getString("tripId");
            type = dataJson.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
//            stopSelf();
            return;
        }
        if (type.equals("taxiArrived")) {
            //show taxi arrived
            ParseUser.getQuery().getInBackground(driverId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    User driver = (User) object;
                    String userProfileImage = null;
                    if (driver != null) {
                        try {
                            text = "";
                            userProfileImage = driver.getProfileImage();
                            String name = driver.getName();
                            text += name + " is here. Look for the taxi and bord.";

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        text = "Taxi arrived. Look for the taxi.";
                    }

                    title = "Taxi arrived";

                    dowloadProfileImage(userProfileImage);
                }
            });
        } else  if (type.equals("destinationArrived")) {
            //show destination arrived
            title = "Reached destination";

            User user = (User)ParseUser.getCurrentUser();
            Location dest = user.getDestLocation();
            if (dest != null) {
                try {
                    dest.fetchIfNeeded();
                    text = "You have reached " + dest.getText();
                } catch (ParseException e) {
                    e.printStackTrace();
                    text = "You have reached destination.";
                }

            } else {
                text = "You have arrived at the destination.";
            }
            showNotification(text, title, userId, driverId, tripId, null);
        }


    }

    private void dowloadProfileImage(final String profileImage) {
        final Handler imageHandler = new Handler(){
            public void handleMessage(Message msg) {
                if(msg.obj!=null && msg.obj instanceof Bitmap){
                    showNotification(text, title, userId, driverId, tripId, (Bitmap)msg.obj);
                }

            };
        };

        new Thread(){
            public void run() {
                try {
                    Bitmap image;
                    if (profileImage != null && profileImage.length() > 0) {
                        URL url = new URL(profileImage);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } else {
                        image = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.profile_avatar);
                    }

//                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(profileImage);
                    Message msg = new Message();
                    msg.obj = image;
                    imageHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void showNotification(String msg, String title, String userId, String driverId, String tripId, Bitmap image) {

        Intent notificationIntent = new Intent(PushNotificationReceiver.INTENT_TO_LAUNCH_MAP);

        PendingIntent contentIntent = PendingIntent.getBroadcast(getApplicationContext(),
                (int) System.currentTimeMillis(),
                notificationIntent,
                Intent.FILL_IN_DATA);

        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_stat_chariot_logo_notification_icon)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(msg);

        if (image != null) {
            builder.setLargeIcon(image);
        }

        Notification n = builder.build();

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(0, n);

    }

}