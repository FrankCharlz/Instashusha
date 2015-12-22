package com.mj.instashusha.utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.mj.instashusha.InstagramApp;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Date;

public class Utils {

    private static final String LAST_URL = "last_insta_url_loaded";
    private static final String PREFS_FILE_NAME = "Christina";

    public static  void saveImage(Context context, ImageView imageView, String save_path) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();

        File save_file = new File(save_path);
        try {
            boolean fcs = save_file.createNewFile();
            InstagramApp.log("File created : " + save_file.getAbsolutePath());
            FileOutputStream ostream = new FileOutputStream(save_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();
            addFileToMediaDatabase(context, save_path);
            InstagramApp.toast(context, "Saved at: " + save_path);

        }
        catch (Exception e) {
            InstagramApp.toast(context, "Imeshindwa kusave picha : "+e.getMessage());
            InstagramApp.log(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void addFileToMediaDatabase(Context context, String file_path) {
        Uri contentUri = Uri.fromFile(new File(file_path));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public static boolean isTheLastUr(Context context, String url) {
        SharedPreferences getPrefs =
                context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        String last_url = getPrefs.getString(LAST_URL, "");
        InstagramApp.log("last url = " + last_url);
        InstagramApp.log("current url = " + url);
        return (url.equalsIgnoreCase(last_url));
    }

    public static void setLastUrl(Context context, String url) {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(LAST_URL, url)
                .apply();
    }

    public static String getTimeStamp() {
        return new Timestamp(new Date().getTime())
                .toString()
                .replaceAll("\\.","_")
                .replaceAll(" ","_");
    }
}
