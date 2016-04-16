package com.mj.instashusha.network;

import com.mj.instashusha.InstagramApp;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Frank on 3/17/2016.
 *
 */
public class Downloader {

    public static void saveStream(InputStream stream, long size, String save_path, ProgressListener progressListener) {

        BufferedInputStream in = null;
        FileOutputStream fout = null;
        long downloaded = 0;
        int percent = 0;

        progressListener.onStart();

        try {
            in = new BufferedInputStream(stream);
            fout = new FileOutputStream(save_path);

            int buffer_size = 1024 * 64;
            final byte data[] = new byte[buffer_size];
            int count;


            while ((count = in.read(data, 0, buffer_size)) != -1) {
                downloaded += count;
                fout.write(data, 0, count);
                percent = (int) (100.0f * downloaded / size);
                progressListener.onProgress(percent);
            }

            //ething good
            progressListener.onCompleted(save_path);

        } catch (Exception e) {
            e.printStackTrace();
            InstagramApp.log("Failed to download : " + e.getLocalizedMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (fout != null) fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }
}
