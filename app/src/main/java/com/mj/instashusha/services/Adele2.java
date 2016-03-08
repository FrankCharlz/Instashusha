package com.mj.instashusha.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;
import com.mj.instashusha.utils.Clip;
import com.mj.instashusha.utils.Utils;

import java.util.List;

/**
 * Created by Frank on 3/4/2016.
 * After being pissed off really bad by PopUpService
 */
public class Adele2 extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
    private static final long INTERVAL_INSTAGRAM_POLL = 10 * 1000L;
    private static final long INTERVAL_CLIPBOARD_POLL =  5 * 1000L;
    private static final int CLIP_POLL_FREQUENCY = 8;
    private int NOTIFICATION_ID = 0;
    private NotificationManager nm;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Context context;
    private ActivityManager am;
    private List<ActivityManager.RunningAppProcessInfo> running_apps;
    private String last_clip_url;
    private ClipboardManager cm;

    @Override
    public void onCreate() {
        super.onCreate();

        am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(this);

        InstagramApp.log("service on-create");

        last_clip_url = Utils.getLastUrl(this);


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
    public void onLowMemory() {
        super.onLowMemory();
        InstagramApp.log("Memory Low, service can be terminated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        InstagramApp.log("service on-start-command");

        //If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onPrimaryClipChanged() {
        showNotif("detected : " + Clip.getInstagramUrl(context));
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            stopSelf(msg.arg1);
        }
    }


    @Override
    public void onDestroy() {
        InstagramApp.log("Service destroyed");
        sendBroadcast(new Intent(BootReceiver.CUSTOM_BROADCAST_ACTION_STRING)); // not killable puta..
        super.onDestroy();
    }
}
