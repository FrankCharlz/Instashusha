package com.mj.instashusha.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.activities.MainActivity;


/**
 * Created by Frank on 3/2/2016.
 * After killing UE
 */
public class PopUpService extends Service {
    private Context context;
    private ClipboardManager cm;


    @Override
    public void onCreate() {
        context = this;
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipListener());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        InstagramApp.log("service started");
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(new ClipListener());


        return START_STICKY; //to ensure we r not killed..
    }


    class ClipListener implements ClipboardManager.OnPrimaryClipChangedListener {
        @Override
        public void onPrimaryClipChanged() {

            final String url = InstagramApp.getLinkFromClipBoard(context);

            if (url.isEmpty()) {
                InstagramApp.log("No instagram url in clipboard");
            }

            if (url.contains("instagram")) {
                context.startActivity(new Intent(context, MainActivity.class));
            }

            InstagramApp.log("clipboard changed..." + url);
            Toast.makeText(getApplicationContext(), "gotchaa..." + url, Toast.LENGTH_SHORT).show();

        }
    }
}
