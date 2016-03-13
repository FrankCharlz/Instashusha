package com.mj.instashusha.services;

import android.content.Context;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.activities.SaveActivity;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.Utils;
import com.squareup.okhttp.Request;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Frank on 3/12/2016.
 * show love, spread it across the country,
 * anyways its 0129 hours, shiiit..
 *
 * TODO: all code in this package is fucked up....
 *
 */
public class PopUpDownloader {


    private static Context context;
    private static String anchor_url;
    private static DownloadCompleteListener listener;

    public static File save(Context context_, String url, DownloadCompleteListener listener_) {
        context = context_;
        anchor_url = url;
        listener = listener_;



        InstagramApp.log("service download procedure  started");

        final Request request = new Request.Builder()
                .url(SaveActivity.BASE_URL + url)
                .build();

        InstagramApp.getOkHttpClient().newCall(request).enqueue(new HttpCallback() {
            //here is main thread....
            @Override
            public void onUrlResponse(InstaResponse ir) {
                InstagramApp.log("response in service : " + ir.toString());
                if (ir.success == 1) {
                    if (ir.video_url.isEmpty()) {
                        saveMedia(ir.image_url);
                    } else {
                        saveMedia(ir.video_url);
                    }
                }
            }

            @Override
            public void onVideoResponse(InputStream stream, long size) throws IOException {
            }
        });


        return null;
    }

    private static void saveMedia(String url) {
        String path = InstagramApp.getAppFolder().getAbsolutePath()+
                File.separator+ Utils.getTimeStamp() + getExtension(url);
        download(url, path);
    }


    private static String getExtension(String url) {
        int urf = url.length();
        return url.substring(urf-4, urf);
    }



    private static void download(final String url, final String path) {
        InstagramApp.log("downloading in service : ");
        InstagramApp.log("from : "+url);
        InstagramApp.log("to : " +path+"\n");
        new Thread(
                new Runnable() {
                    @Override
                    public void run()  {
                        BufferedInputStream in = null;
                        FileOutputStream fout = null;
                        try {
                            in = new BufferedInputStream(new URL(url).openStream());
                            fout = new FileOutputStream(path);

                            int buffer_size = 1024 * 64;
                            final byte data[] = new byte[buffer_size];
                            int count;

                            while ((count = in.read(data, 0, buffer_size)) != -1) {
                                fout.write(data, 0, count);
                            }

                            in.close();
                            fout.close();

                            listener.done(anchor_url, path);

                            InstagramApp.log("service download completed successfully");
                            InstagramApp.log("from : "+url);
                            InstagramApp.log("to : " +path+"\n");

                        } catch (IOException e) {
                            InstagramApp.log("downloading in service failed: "+e.getLocalizedMessage());
                            e.printStackTrace();

                        } finally {
                            //if (in != null) in.close();
                            //if (fout != null) fout.close();
                        }
                    }
                }, "Service downloading thread"
        ).start();
    }


    public interface DownloadCompleteListener {
        void done(String url, String path);
    }

}
