package com.mj.instashusha.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.mj.instashusha.MyApp;

/**
 * Created by Frank on 7/10/2016.
 * high cohesion
 */
public class ServiceMonitor {

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void checkAndStartBackgroundService(Context context) {
        //check if service was started successfully, if not start it now...
        Intent serviceIntent = new Intent(context, PopUpService.class);
        boolean sup = isServiceRunning(context, PopUpService.class);
        if (!sup) {
            MyApp.log("service was not running, I gotta start it..");
            context.startService(serviceIntent);
        } else {
            MyApp.log("service was still running");
        }
    }

}
