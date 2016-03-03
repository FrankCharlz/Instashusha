package com.mj.instashusha.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;

/**
 * Created by Frank on 3/4/2016.
 * After being ppissed off realy bad by PopUpService
 */
public class Adele extends Service {

    private NotificationManager nm;
    private int NOTIFICATION_ID = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotif("adele 1st time");

    }

    private void showNotif(String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notif: "+ NOTIFICATION_ID)
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();

        nm.notify(NOTIFICATION_ID++, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InstagramApp.log("service start command called...");
        showNotif("--holla again..");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
