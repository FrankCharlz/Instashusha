package com.mj.instashusha.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;


/**
 * Created by Frank on 3/2/2016.
 * After killing UE
 */
public class PopUpService extends Service {
    private Context context;
    private ClipboardManager cm;
    private NotificationManager nm;
    private int NOTIFICATION = 0x0a;
    private View popUpView;
    private WindowManager windowManager;
    private LayoutInflater inflater;


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification("started");

        prepareView();



        windowManager.addView(popUpView, params);

    }

    private void prepareView() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        popUpView  = inflater.inflate(R.layout.service_pop_up, null);


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 64;
        params.y = 64;
    }


    private void showNotification(String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_arrow_forward_white_24px)
                .setContentTitle("Instashusha")
                .setContentText("Service is running: \n"+msg)
                .setContentIntent(contentIntent)
                .build();

        nm.notify(NOTIFICATION++, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InstagramApp.log("service start command");
        cm.addPrimaryClipChangedListener(clipListener);
        return START_STICKY; //to ensure we are not killed..
    }

    private ClipboardManager.OnPrimaryClipChangedListener clipListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    String url = InstagramApp.getLinkFromClipBoard(context);

                    if (url.isEmpty()) {
                        InstagramApp.log("No instagram url in clipboard");
                        url += "\nnon instagram url";
                    }

                    if (url.contains("instagram")) {
                        //context.startActivity(new Intent(context, MainActivity.class));
                    }


                    InstagramApp.log("clipboard changed..." + url);
                    showNotification("got url : "+url);
                }
            };

    @Override
    public void onDestroy() {

        showNotification("service destroy...");

        if (cm != null) {
            cm.removePrimaryClipChangedListener(clipListener);
        }

        InstagramApp.log("service destroyed..");
        nm.cancel(NOTIFICATION);

        super.onDestroy();
    }
}
