package com.mj.instashusha.services;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mj.instashusha.R;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.Utils;

import java.io.File;

/**
 * Created by Frank on 3/12/2016.
 * -kemmy's tycs graduation @ chief sec school...
 */
public class PopUpView {

    private final Context context;
    private WindowManager windowManager;
    private View popUpView;
    private WindowManager.LayoutParams params;
    private String url;
    private DopeTextView share, save, content;

    public PopUpView(Context context) {
        this.context = context;
        init();
    }

    private  void init() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        popUpView = inflater.inflate(R.layout.service_pop_up, null);


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 64;
        params.y = 64;


        ImageView clear = (ImageView) popUpView.findViewById(R.id.imgv_popup_clear);
        clear.setOnClickListener(clickListener);

        save = (DopeTextView) popUpView.findViewById(R.id.btn_popup_save);
        share = (DopeTextView) popUpView.findViewById(R.id.btn_popup_share);
        content = (DopeTextView) popUpView.findViewById(R.id.popup_content);

        save.setOnClickListener(clickListener);
        share.setOnClickListener(clickListener);


    }

    public void show() {
        windowManager.addView(popUpView, params);
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
        windowManager.removeView(popUpView);
    }

    private void doSave() {
        removeView();
        PopUpDownloader.save(url, new PopUpDownloader.DownloadCompleteListener() {
            @Override
            public void done(String anchor_url, String path) {
                Utils.setLastUrl(context, anchor_url);
                Utils.addFileToMediaDatabase(context, path);
            }
        });

    }

    private void doShare() {
        removeView();
        PopUpDownloader.save(url, new PopUpDownloader.DownloadCompleteListener() {
            @Override
            public void done(String anchor_url, String path) {
                Utils.setLastUrl(context, anchor_url);
                Utils.addFileToMediaDatabase(context, path);
                Sharer.share(context, new File(path));
            }
        });
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content_text) {
        content_text = content_text + url;
        content.setText(content_text);
    }
}
