package com.mj.instashusha.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mj.instashusha.MyApp;

/**
 * Created by Frank on 7/10/2016.
 *
 */
public class Prefs {


    public static final String PREFS_FILE_NAME = "FhYmkF";
    private static final String LAST_URL = "last_insta_url_loaded";
    private static final String DOWNLOADED_MEDIA_COUNT = "bj73m";
    private static final String HAS_POSTED_EMAIL = "B89hiun2";
    private static final String FIRST_LAUNCH = "cS9ib892";


    public static int getNumberOfSavedMedia(Context context) {
        return context
                .getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getInt(DOWNLOADED_MEDIA_COUNT, 0);
    }


    public static void incrementDownloadedMedia(Context context) {
        int current_number = getNumberOfSavedMedia(context);
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(DOWNLOADED_MEDIA_COUNT, current_number + 1)
                .apply();
    }


    public static void setLastUrl(Context context, String url) {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(LAST_URL, url)
                .apply();
    }


    public static boolean isTheLastUr(Context context, String url) {
        String last_url = getLastUrl(context);
        MyApp.log("last url = " + last_url);
        MyApp.log("current url = " + url);
        return (url.equalsIgnoreCase(last_url));
    }

    public static String getLastUrl(Context context) {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getString(LAST_URL, "x");
    }

    public static boolean hasPostedEmail(Context context) {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .getBoolean(HAS_POSTED_EMAIL, true);
    }

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        boolean res = prefs.getBoolean(FIRST_LAUNCH, false);

        //take care of one time ops... may be slow //// TODO: 7/19/2016 optimize
        prefs.edit().putBoolean(FIRST_LAUNCH, false).apply();
        prefs.edit().putInt(DOWNLOADED_MEDIA_COUNT, 0 /*MyApp.getAppFolder().list().length*/).apply();
        return res;
    }

    public static void setSentEmailTrue(Context context) {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(HAS_POSTED_EMAIL, true)
                .apply();
    }
}
