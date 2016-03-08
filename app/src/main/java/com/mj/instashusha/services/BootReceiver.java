package com.mj.instashusha.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mj.instashusha.activities.MainActivity;

import java.util.Calendar;

/**
 * Created by Frank on 3/2/2016.
 * After drinking a glass of cold water... @23:16/tabata/segerea/chama
 */
public class BootReceiver extends BroadcastReceiver{
    private static final long REPEATING_INTERVAL = 20L * 1000;
    public static final String CUSTOM_BROADCAST_ACTION_STRING = "com.mj.instashusha.CUSTOM_BOOT_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
        //starts pop up service when the phone boots..

        String intent_action = intent.getAction().toString();
        boolean received = intent_action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent_action.equalsIgnoreCase(CUSTOM_BROADCAST_ACTION_STRING);

        if (received) {
            Intent serviceIntent = new Intent(context, Adele.class);
            context.startService(serviceIntent);
        }

    }
}
