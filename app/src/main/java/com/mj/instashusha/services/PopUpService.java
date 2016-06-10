package com.mj.instashusha.services;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.utils.Clip;
import com.mj.instashusha.utils.Utils;

public class PopUpService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private ClipboardManager cm;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        InstagramApp.log("service on-create");
        context = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InstagramApp.log("service on-start-command");
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(this);

        return START_STICKY;
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
        String url = Clip.hasInstagramUrl(context);
        String last_url = Utils.getLastUrl(context);

        if (url.isEmpty()) return; //return if not insta url

        url = InstagramApp.processUrl(url);
        if (url.equals(last_url)) return; //return if the same url

        /* the found url is new */

        InstagramApp.log("Found: " + url);

        PopUpView view = new PopUpView(context);
        view.setUrl(url);
        view.show();

    }


}

