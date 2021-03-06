package com.mj.instashusha.services;


import com.mj.instashusha.Constants;
import com.mj.instashusha.MyApp;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Request;

/**
 * Created by Frank on 3/12/2016.
 * show love, spread it across the country,
 * anyways its 0129 hours, shiiit..
 * <p/>
 * TODO: all code in this package is fucked up....
 */
public class PopUpDownloader {


    private static String anchor_url;
    private static DownloadCompleteListener listener;

    public static void save(String url, DownloadCompleteListener listener_) {
        anchor_url = url;
        listener = listener_;


        MyApp.log("service saveStream procedure  started");

        final Request request = new Request.Builder()
                .url(Constants.BASE_URL + url)
                .build();

        MyApp.getOkHttpClient().newCall(request).enqueue(new HttpCallback() {
            //here is main thread....
            @Override
            public void onUrlResponse(InstaResponse ir) {
                MyApp.log("response in service : " + ir.toString());
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

    }

    private static void saveMedia(String url) {
        String path = MyApp.getAppFolder().getAbsolutePath() +
                File.separator + Utils.getTimeStamp() + getExtension(url);
        download(url, path);
    }


    private static String getExtension(String url) {
        int urf = url.length();
        return url.substring(urf - 4, urf);
    }


    private static void download(final String url, final String path) {
        MyApp.log("downloading in service : ");
        MyApp.log("from : " + url);
        MyApp.log("to : " + path + "\n");
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        BufferedInputStream in;
                        FileOutputStream fout;
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

                            MyApp.log("service saveStream completed successfully");
                            MyApp.log("from : " + url);
                            MyApp.log("to : " + path + "\n");

                        } catch (IOException e) {
                            MyApp.log("downloading in service failed: " + e.getLocalizedMessage());
                            e.printStackTrace();

                        }
                    }
                }, "Service downloading thread"
        ).start();
    }


    public interface DownloadCompleteListener {
        void done(String url, String path);
    }

}
