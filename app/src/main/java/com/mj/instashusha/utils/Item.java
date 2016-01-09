package com.mj.instashusha.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import java.io.File;

/**
 * Created by Frank on 1/9/2016.
 */
public class Item {
    public long date;
    public boolean isImage;
    public String name;
    public Bitmap thumbnail;
    public File file;

    public Item(File f) {
        file = f;
        date = f.lastModified();
        name = f.getName();
        isImage = name.endsWith(".png");
        if (!isImage) {
            thumbnail = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), 0); //if video load kabiisaa
        }
    }
}