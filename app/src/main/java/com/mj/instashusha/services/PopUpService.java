package com.mj.instashusha.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;
import com.mj.instashusha.utils.Clip;
import com.mj.instashusha.utils.Utils;

public class PopUpService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private ClipboardManager cm;
    private int NOTIFICATION_ID = 0;
    private NotificationManager nm;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        InstagramApp.log("service on-create");
        context = this;
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
        InstagramApp.log("service on-start-command");
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        cm.addPrimaryClipChangedListener(this);

        return  START_STICKY;
    }

    @Override
    public void onLowMemory() {
        InstagramApp.log("Memory Low, service can be terminated");
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        InstagramApp.log("Service destroyed");
        super.onDestroy();
        if (cm != null) {
            cm.removePrimaryClipChangedListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrimaryClipChanged() {
        InstagramApp.log("detected clip changed");
        String url = Clip.getInstagramUrl(context);
        String last_url = Utils.getLastUrl(context);

        if (url.isEmpty()) return; //return if not insta url
        if (url.equals(last_url)) return; //return if the same url

        /* the found url is new */

        InstagramApp.log("Found: " + url);

        PopUpView view = new PopUpView(context);
        view.setUrl(url);
        view.setContent("");
        view.show();

    }


}

