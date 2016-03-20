package com.xc0ffeelabs.taxicab.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xc0ffeelabs.taxicab.activities.MapsActivity;

import java.util.List;

public class IntentToLaunchMapReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NAYAN", "onReceive of intent to launch map");
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo info : taskInfo) {
            ComponentName componentInfo = info.topActivity;
            if (componentInfo.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                Log.d("NAYAN", "Send broadcast to reorder map activity");
                sendBroadcast(context);
                return;
            }
        }

        Log.d("NAYAN", "Start new activity");
        Intent intentMap = new Intent(context, MapsActivity.class);
        intentMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMap);
    }

    private void sendBroadcast(Context context) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(PushNotificationReceiver.REQUEST_LAUNCH_MAP);
        broadcastManager.sendBroadcast(intent);
    }
}
