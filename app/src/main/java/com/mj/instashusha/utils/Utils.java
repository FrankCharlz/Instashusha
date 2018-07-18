package com.mj.instashusha.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;

import com.mj.instashusha.MyApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
            MyApp.log("File created : " + save_file.getAbsolutePath());
            FileOutputStream ostream = new FileOutputStream(save_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            MyApp.log("File downloaded : "+save_path);
            ostream.close();

        } catch (Exception e) {
            MyApp.log(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void addFileToMediaDatabase(Context context, String path) {
        Uri contentUri = Uri.fromFile(new File(path));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        MyApp.log("Added to media database : "+path);
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


    public static String processUrl(String text) {
        MyApp.log("processing url : "+text);
        int _start = text.indexOf("https://www.instagram.com");
        int _space = text.indexOf(" ", (_start + 1));
        int _ending = text.length();

        if (_space != -1) _ending = _space; //if contains space end at space
        if (_start == -1) _start = 0; //foul proof

        MyApp.log("processed url : "+text.substring(_start, _ending));
        return text.substring(_start, _ending);
    }


    public static int numberOfDownloaded() {
        File folder = MyApp.getAppFolder();
        if (folder != null && folder.listFiles() != null) return folder.listFiles().length;
        return  0;
    }
}
