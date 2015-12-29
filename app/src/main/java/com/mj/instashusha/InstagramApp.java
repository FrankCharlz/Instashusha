package com.mj.instashusha;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import org.codechimp.apprater.AppRater;

import java.io.File;

/**
 * Created by Frank on 12/18/2015.
 */
public class InstagramApp extends Application {

    private static final long SIZE_OF_OKHTTP_CACHE = 10 * 1024 * 1024; //10MB
    private final static OkHttpClient okhttpClient = new OkHttpClient();
    public static final String GO_TO_INSTRUCTIONS = "hgGHGy";

    public static String PHOTO_FOLDER_PATH, VIDEO_FOLDER_PATH;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Cache responseCache = new Cache(getApplicationContext().getCacheDir(), SIZE_OF_OKHTTP_CACHE);
            okhttpClient.setCache(responseCache);
        } catch (Exception e) {
            log("Unable to set http cache"+ e);
        }

    }

    public  static OkHttpClient getOkHttpClient() {
        return okhttpClient;
    }
    public static void log(String str){
        Log.e("insta-dl", str);
    }

    public static void toast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    }

    public static String getLinkFromClipBoard(Context context) {
        //should return link or empty string
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String r = "";
        if(cm.hasPrimaryClip()) {
            ClipData data = cm.getPrimaryClip();
            ClipData.Item item = data.getItemAt(0);
            r = item.getText().toString();

            if (!isValidInstaLink(r)) r = "";
        }

        return r;
    }

    private static boolean isValidInstaLink(String url) {
        return url.contains("instagram");
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File createInstaShushaPhotosFolder(String folderName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), folderName);
        if (!file.mkdirs()) {
            InstagramApp.log("Photos folder not created");
        }
        return file;
    }

    public static File createInstaShushaVideosFolder(String folderName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), folderName);
        if (!file.mkdirs()) {
            InstagramApp.log("Videos folder not created");
        }
        return file;
    }


    public static void makeAppFolder() {
        //called on MainActivity
        File photoFolder = createInstaShushaPhotosFolder("InstashushaPicha");
        File videosFolder = createInstaShushaVideosFolder("InstashushaVideos");

        PHOTO_FOLDER_PATH = photoFolder.getAbsolutePath();
        VIDEO_FOLDER_PATH = videosFolder.getAbsolutePath();

    }


}
