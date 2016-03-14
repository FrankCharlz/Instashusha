package com.mj.instashusha.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mj.instashusha.Constants;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.Media;
import com.mj.instashusha.utils.Utils;

import java.io.File;
import java.util.Random;

/**
 * Created by Frank on 3/12/2016.
 * -kemmy's tycs graduation @ chief sec school...
 */
public class PopUpView {

    private final Context context;
    private final NotificationManager nm;
    private final WindowManager windowManager;
    private final LayoutInflater inflater;

    private View popView;
    private WindowManager.LayoutParams params;
    private String url;
    private DopeTextView share, save, content, adTv;
    private CharSequence adWords = "More offers from Tigo";
    private CharSequence adUrl = "www.google.com";
    private int NOTIFICATION_ID = 0x00c;

    public PopUpView(Context context) {
        this.context = context;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private  void init() {

        popView = inflater.inflate(R.layout.service_pop_up, null);


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 64;
        params.y = 64;


        ImageView clear = (ImageView) popView.findViewById(R.id.imgv_popup_clear);
        clear.setOnClickListener(clickListener);

        save = (DopeTextView) popView.findViewById(R.id.btn_popup_save);
        share = (DopeTextView) popView.findViewById(R.id.btn_popup_share);
        content = (DopeTextView) popView.findViewById(R.id.popup_content);
        adTv = (DopeTextView) popView.findViewById(R.id.popup_ad);

        save.setOnClickListener(clickListener);
        share.setOnClickListener(clickListener);

        //decorating ads in popup..
        Spannable spannableAd = new SpannableString(adWords);
        spannableAd.setSpan(new UnderlineSpan(), 0, adWords.length(), 0);
        adTv.setText(spannableAd);

        adTv.setOnClickListener(new AdTvClicked());

    }

    public void show() {
        windowManager.addView(popView, params);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.imgv_popup_clear :  removeView(); break;
                case R.id.btn_popup_save: doSave(); break;
                case R.id.btn_popup_share: doShare(); break;
                default: break;
            }
        }
    };

    private void removeView() {
        windowManager.removeView(popView);
    }

    private void doSave() {
        removeView();
        final int notification_id = new Random().nextInt(22);
        showNotif(notification_id, "Downloading", ""+url);

        PopUpDownloader.save(url, new PopUpDownloader.DownloadCompleteListener() {
            @Override
            public void done(String anchor_url, String path) {
                Utils.setLastUrl(context, anchor_url);
                Utils.addFileToMediaDatabase(context, path);
                nm.cancel(notification_id);
                showNotifCompleted(notification_id+99, path);
            }
        });

    }


    private void doShare() {
        removeView();
        ////TODO: for now just use mainactivity for this...
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content_text) {
        content_text = content_text + url;
        content.setText(content_text);
    }

    private void showNotif(int notification_id, String title, String content) {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("")
                .setContentTitle(title)
                .setContentText(content)
                .setOngoing(true)
                .build();
        nm.notify(notification_id, notification);
    }

    private void showNotifCompleted(int id, String path) {
        String mime = Media.getMimeType(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)), mime);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_file_download)
                .setContentTitle("Download completed")
                .setContentText("Download completed")
                .setContentIntent(pendingIntent)
                .build();
        nm.notify(id, notification);
    }

    private class AdTvClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //open browser...
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Constants.TIGO_URL));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}