package com.mj.instashusha.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Frank on 3/2/2016.
 * After drinking a glass of cold water... @23:16/tabata/segerea/chama
 */
public class BootReceiver extends BroadcastReceiver{
    private static final long REPEATING_INTERVAL = 30L * 1000;
    public static final String CUSTOM_BROADCAST_ACTION_STRING = "com.mj.instashusha.CUSTOM_BOOT_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
        //starts pop up service when the phone boots..
        boolean received = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().toString().equalsIgnoreCase(CUSTOM_BROADCAST_ACTION_STRING);

        if (received) {
            Intent serviceIntent = new Intent(context, Adele.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    serviceIntent,
                    0 //i dont know
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    5000L, //trigger after 0 seconds
                    REPEATING_INTERVAL,
                    pIntent
            );
        }

    }
}
