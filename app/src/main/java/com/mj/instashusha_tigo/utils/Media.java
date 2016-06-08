package com.mj.instashusha_tigo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Frank on 3/14/2016.
 */
public class Media {
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_IMAGE = "image/*";

    public static boolean isImage(String fileName) {
        return fileName.endsWith("jpg") || fileName.endsWith("png") || fileName.endsWith("gif");
    }

    public static void openItem(Context context, File file) {
        String mime = Media.getMimeType(file);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), mime);
        context.startActivity(intent);
    }


    public static String getMimeType(File file) {
        return getMimeType(file.getName());
    }

    public static String getMimeType(String path) {
        return isImage(path) ? MIME_TYPE_IMAGE : MIME_TYPE_VIDEO;
    }
}
