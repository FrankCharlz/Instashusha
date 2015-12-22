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

    private static final long SIZE_OF_OKHTTP_CACHE = 5 * 1024 * 1024;
    private final static OkHttpClient okhttpClient = new OkHttpClient();

    private static String sdcard_path = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String photo_folder_path = sdcard_path+"/InstaShusha/InstaShusha Picha/";
    private static String video_folder_path = sdcard_path+"/InstaShusha/InstaShusha Video/";

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
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
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

    public static void makeAppFolder() {
        File[] folders = {
                new File(sdcard_path+"/InstaShusha/InstaShusha Video/"),
                new File(sdcard_path+"/InstaShusha/InstaShusha Picha/")
        };

        for (File f : folders) {
            if(!f.exists()) f.mkdirs();
        }
    }

    public static String getAppPhotoFolder() {
        return  photo_folder_path;
    }

    public static String getAppVideoFolder() {
        return  video_folder_path;
    }
    public static String getAppFolder() {
        return  sdcard_path+"/InstaShusha/";
    }



}
