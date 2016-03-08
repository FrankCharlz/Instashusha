package com.mj.instashusha.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.mj.instashusha.activities.SaveActivity;
import com.mj.instashusha.utils.Utils;

import java.util.List;

/**
 * Created by Frank on 3/4/2016.
 * After being pissed off really bad by PopUpService
 */
public class Adele extends Service {

    private static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
    private static final long INTERVAL_INSTAGRAM_POLL = 22 * 1000L;
    private static final long INTERVAL_CLIPBOARD_POLL =  5 * 1000L;
    private NotificationManager nm;
    private int NOTIFICATION_ID = 0;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Context context;
    private ActivityManager am;
    private List<ActivityManager.RunningAppProcessInfo> running_apps;

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        InstagramApp.log("service on-create");

        am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        HandlerThread instaPollThread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        instaPollThread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = instaPollThread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);


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

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        //If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isInstagramRunning() {
        running_apps = am.getRunningAppProcesses();
        for (int i = 0; i < running_apps.size(); i++) {
            //// TODO: 3/4/2016 i think i should starts at the tail up i.e i--
            if(running_apps.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE &&
                    running_apps.get(i).processName.equals(INSTAGRAM_PACKAGE_NAME)) {
                InstagramApp.log("got instagram running at: " +i+"/"+running_apps.size()+": "
                        +running_apps.get(i).processName);
                InstagramApp.log("importance: "+running_apps.get(i).importance);
                return true;
            }
        }
        return false;
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            boolean insta_running = isInstagramRunning();
            long sleep_time;
            int polls_made = 9;

            while (true) {

                if (insta_running) {
                    pollClips();
                    polls_made--;
                    if (polls_made == 0) {
                        //guarantee than on the next loop, the else part shall be executed
                        //hence polling insta again
                        //resetting polls
                        insta_running = false;
                        polls_made = 9;
                    }
                    InstagramApp.log("polling clips "+(9-polls_made)+": "+System.currentTimeMillis());
                    sleep_time = INTERVAL_CLIPBOARD_POLL;
                } else {
                    insta_running = isInstagramRunning();
                    InstagramApp.log("polling insta "+System.currentTimeMillis());
                    sleep_time = insta_running ? INTERVAL_CLIPBOARD_POLL : INTERVAL_INSTAGRAM_POLL;
                }

                try {
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                //stopSelf(msg.arg1);
            }
        }
    }

    private String clip_text;
    private void pollClips() {
        clip_text = InstagramApp.getLinkFromClipBoard(context);
        if (!clip_text.isEmpty()) {
            InstagramApp.log("Got url: " +clip_text);
            if (!Utils.isTheLastUr(context, clip_text)) {
                showNotif("Found url: \n"+clip_text);
            }
        }
    }

    @Override
    public void onDestroy() {
        InstagramApp.log("Service destroyed");
        sendBroadcast(new Intent(BootReceiver.CUSTOM_BROADCAST_ACTION_STRING)); // not killable puta..
        super.onDestroy();
    }
}
