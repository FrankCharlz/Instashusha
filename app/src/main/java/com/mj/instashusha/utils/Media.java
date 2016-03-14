package com.mj.instashusha.utils;

import java.io.File;

/**
 * Created by Frank on 3/14/2016.
 *
 *
 */
public class Media {
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_IMAGE = "image/*";

    public static boolean isImage(String fileName) {
        return fileName.endsWith("jpg") || fileName.endsWith("png") || fileName.endsWith("gif");
    }

    public static String getMimeType(File file) {
        return isImage(file.getName()) ? MIME_TYPE_IMAGE : MIME_TYPE_VIDEO;
    }
}
