package com.mj.instashusha;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Frank on 12/18/2015.
 * <p/>
 * TODO: migrate some methods from here to Utils
 */
public class InstagramApp extends Application {
    public static final String GO_TO_INSTRUCTIONS = "hgGHGy";
    private static final String APP_FOLDER_NAME = "Instashusha/";
    private static final long SIZE_OF_OKHTTP_CACHE = 20 * 1024 * 1024; //20MB
    public static boolean BACK_FROM_SAVE_ACTIVITY = false;
    private static OkHttpClient okhttpClient;
    private Tracker mTracker;

    public static int mediaDownloaded() {
        return InstagramApp.getAppFolder().listFiles().length;
    }

    public static OkHttpClient getOkHttpClient() {
        return okhttpClient;
    }

    public static void log(String str) {
        Log.e("instashusha", str);
    }

    public static File getAppFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_FOLDER_NAME);
        if (file.mkdirs()) {
            InstagramApp.log("Folder created");
        }
        return file;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Cache okCache = new Cache(getCacheDir(), SIZE_OF_OKHTTP_CACHE);
        okhttpClient = new OkHttpClient.Builder()
                .cache(okCache)
                .build();

    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static String processUrl(String text) {
        InstagramApp.log("processing url : "+text);
        int _start = text.indexOf("https://www.instagram.com");
        int _space = text.indexOf(" ", (_start + 1));
        int _ending = text.length();

        if (_space != -1) _ending = _space; //if contains space end at space
        if (_start == -1) _start = 0; //foul proof

        InstagramApp.log("processed url : "+text.substring(_start, _ending));
        return text.substring(_start, _ending);
    }
    

}


