package com.mj.instashusha.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.mj.instashusha.InstagramApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {


    public static void saveImage(ImageView imageView, String save_path) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        File save_file = new File(save_path);
        try {
            boolean fc = save_file.createNewFile();
            if (!fc) throw new FileNotFoundException("Failed to create file");
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


    public static String getTimeStamp() {
        final String TIME_STAMP_FORMAT = "yyMMddHHmmss";
        return new SimpleDateFormat(TIME_STAMP_FORMAT, Locale.UK).format(new Date());
    }

    public static int addAlphaToColor(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    public static int getColorCampat(Context context, int cid) {
        return ContextCompat.getColor(context, cid);
    }




}
