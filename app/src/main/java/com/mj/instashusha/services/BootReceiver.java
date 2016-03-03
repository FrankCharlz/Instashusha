package com.mj.instashusha.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mj.instashusha.utils.Utils;

/**
 * Created by Frank on 3/2/2016.
 * After drinking a glass of cold water... @23:16/tabata/segerea/chama
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //starts pop up service when the phone boots..
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, PopService.class);
            context.startService(serviceIntent);

        }

    }
}
