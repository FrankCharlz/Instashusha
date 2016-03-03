package com.mj.instashusha.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mj.instashusha.InstagramApp;


/**
 * Created by Frank on 3/2/2016.
 * After killing UE
 */
public class PopService extends Service {
    private Context context;
    private Looper sLooper;
    private BossHandler sHandler;


    @Override
    public void onCreate() {
        //Process.THREAD_PRIORITY_BACKGROUND
        HandlerThread handlerThread = new HandlerThread("SERVICE_THREAD", Process.THREAD_PRIORITY_BACKGROUND);

        handlerThread.start();

        sLooper = handlerThread.getLooper();
        sHandler = new BossHandler(sLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InstagramApp.log("service started");
        Toast.makeText(this, "servicing...", Toast.LENGTH_SHORT).show();

        Message msg = sHandler.obtainMessage();
        msg.arg1 = startId;
        sHandler.sendMessage(msg);

        return START_STICKY; //to ensure we r not killed..
    }

    class BossHandler extends Handler {

        public BossHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.addPrimaryClipChangedListener(new ClipListener());
        }
    }

    class ClipListener implements ClipboardManager.OnPrimaryClipChangedListener {
        @Override
        public void onPrimaryClipChanged() {
            InstagramApp.log("clipboard changed...");
            Toast.makeText(getApplicationContext(), "servicing...", Toast.LENGTH_SHORT).show();
        }
    }
}
