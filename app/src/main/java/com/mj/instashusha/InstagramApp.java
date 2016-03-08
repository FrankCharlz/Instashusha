package com.mj.instashusha;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.io.File;

/**
 * Created by Frank on 12/18/2015.
 *
 * TODO: migrate some methods from here to Utils
 */
public class InstagramApp extends Application {
    private static final String APP_FOLDER_NAME = "Instashusha/";
    public static  boolean BACK_FROM_SAVE_ACTIVITY = false;
    private Tracker mTracker;

    private static final long SIZE_OF_OKHTTP_CACHE = 20 * 1024 * 1024; //20MB
    private final static OkHttpClient okhttpClient = new OkHttpClient();
    public static final String GO_TO_INSTRUCTIONS = "hgGHGy";

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
    public static int mediaDownloaded() {
        return InstagramApp.getAppFolder().listFiles().length;
    }

    public  static OkHttpClient getOkHttpClient() {
        return okhttpClient;
    }

    public static void log(String str){
        Log.e("instashusha", str);
    }


    public static File getAppFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+APP_FOLDER_NAME);
        if (file.mkdirs()) {
            InstagramApp.log("Folder created");
        }
        return file;
    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

}
