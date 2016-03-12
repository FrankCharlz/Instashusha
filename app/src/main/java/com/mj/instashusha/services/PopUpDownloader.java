package com.mj.instashusha.services;

import android.content.Context;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.activities.SaveActivity;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.Utils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by Frank on 3/12/2016.
 * show love, spread it across the country,
 * anyways its 0129 hours, shiiit..
 *
 */
public class PopUpDownloader {


    private static Context context;
    private static String anchor_url;

    public static File save(Context context_, String url) {
        context = context_;
        anchor_url = url;

        final Request request = new Request.Builder()
                .url(SaveActivity.BASE_URL + url)
                .build();

        InstagramApp.getOkHttpClient().newCall(request).enqueue(new HttpCallback() {
            @Override
            public void onUrlResponse(InstaResponse ir) {
                InstagramApp.log("response in service : "+ir.toString());
                if (ir.success == 1) {
                    if(ir.video_url.isEmpty()) {
                        saveImage(ir.image_url);
                    } else {
                        saveVideo(ir.video_url);
                    }
                }

            }

            @Override
            public void onVideoResponse(InputStream stream, long size) throws IOException {
            }
        });


        return null;
    }


    private static void saveImage(String image_url) {
        String path = InstagramApp.getAppFolder().getAbsolutePath()+
                File.separator+ Utils.getTimeStamp() + getExtension(image_url);
        try {
            download(image_url, path);
        } catch (IOException e) {
            InstagramApp.log("downloading image failed: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private static void saveVideo(String video_url) {
        String path = InstagramApp.getAppFolder().getAbsolutePath()+
                File.separator+ Utils.getTimeStamp() + getExtension(video_url);
        try {
            download(video_url, path);
        } catch (IOException e) {
            InstagramApp.log("downloading video failed: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private static String getExtension(String url) {
        int urf = url.length();
        return url.substring(urf-4, urf);
    }



    private static void download(String url_, String path) throws IOException {
        InstagramApp.log("\n downloading in service : ");
        InstagramApp.log(url_);
        InstagramApp.log(path+"\n");

        URL url = new URL(url_);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        Utils.setLastUrl(context, anchor_url);
        context = null; //garbage collector
    }
}
