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

public class Utils {

    private static final String LAST_URL = "last_insta_url_loaded";

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

        /*
        MediaScannerConnection.scanFile(context,
                new String[]{file_path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        InstagramApp.log("File is now on gallery");
                    }
                });
        */
    }


    public static boolean isTheLastUr(Context context, String url) {
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        String last_url = getPrefs.getString(LAST_URL, "");
        return (url.equalsIgnoreCase(last_url));
    }

    public static void setLastUrl(Context context, String url) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(LAST_URL, url)
                .apply();
    }
}
