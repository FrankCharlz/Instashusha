package com.mj.instashusha.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;

import java.util.HashMap;

/**
 * Created by Frank on 1/11/2016.
 */
public class VideoThumbnailCache {

    private static HashMap<String, Bitmap> thumbCache = new HashMap<>();

    public static Bitmap getBitmap(String path) {

        Bitmap bitmap = thumbCache.get(path);

        if (bitmap == null) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path, 0);
            thumbCache.put(path, bitmap);
        }
        return bitmap;
    }
}
