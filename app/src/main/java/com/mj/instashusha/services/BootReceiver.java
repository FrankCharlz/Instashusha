package com.mj.instashusha.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Frank on 3/2/2016.
 * After drinking a glass of cold water... @23:16/tabata/segerea/chama
 */
public class BootReceiver extends BroadcastReceiver {
    public static final String CUSTOM_BROADCAST_ACTION_STRING = "com.mj.instashusha.CUSTOM_BOOT_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
        //starts pop up service when the phone boots..

        String intent_action = intent.getAction();
        boolean received = intent_action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent_action.equalsIgnoreCase(CUSTOM_BROADCAST_ACTION_STRING);

        if (received) {
            Intent serviceIntent = new Intent(context, PopUpService.class);
            context.startService(serviceIntent);
        }


    }

}
