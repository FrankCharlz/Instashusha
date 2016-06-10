package com.mj.instashusha.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.mj.instashusha.Constants;
import com.mj.instashusha.InstagramApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;

public class Utils {

    public static final String PREFS_FILE_NAME = "FhYmkF";
    private static final String LAST_URL = "last_insta_url_loaded";
    private static final String DOWNLOADED_MEDIA_COUNT = "bj73m";

    public static void saveImage(ImageView imageView, String save_path) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        File save_file = new File(save_path);
        try {
            save_file.createNewFile();
            InstagramApp.log("File created : " + save_file.getAbsolutePath());
            FileOutputStream ostream = new FileOutputStream(save_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            InstagramApp.log("File downloaded : "+save_path);
            ostream.close();

        } catch (Exception e) {
            InstagramApp.log(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void addFileToMediaDatabase(Context context, String path) {
        Uri contentUri = Uri.fromFile(new File(path));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        InstagramApp.log("Added to media database : "+path);
    }

    public static void addPathToDB(Context context, String path) {
        try {
            FileOutputStream dbOutStream = context.openFileOutput(Constants.DB_NAME, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dbOutStream));
            writer.write(path.trim());
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            InstagramApp.log("Failed to add path to db");
        }
    }

    public static void incrementDownloadedMedia(Context context) {
        int current_number = getDownloadedMediaCount(context);
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(DOWNLOADED_MEDIA_COUNT, current_number + 1)
                .apply();
    }

    public static int getDownloadedMediaCount(Context context) {
        return context
                .getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getInt(DOWNLOADED_MEDIA_COUNT, 0);
    }


    public static boolean isTheLastUr(Context context, String url) {
        String last_url = getLastUrl(context);
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
                .substring(0, 20)
                .trim()
                .replaceAll("\\.", "_")
                .replaceAll(":", "")
                .replaceAll("-", "")
                .replaceAll(" ", "");


    }

    public static int addAlphaToColor(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    public static int getColor(Context context, int cid) {
        return ContextCompat.getColor(context, cid);
    }

    public static String getLastUrl(Context context) {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getString(LAST_URL, "x");
    }


}
