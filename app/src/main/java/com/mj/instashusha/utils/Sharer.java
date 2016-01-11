package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mj.instashusha.R;
import com.mj.instashusha.activities.SaveActivity;

import java.io.File;

/**
 * Created by Frank on 1/9/2016.
 */

public class Sharer {

    public static void share(Context context, File file) {
        String mime_type = (file.getName().endsWith("png"))
                ? SaveActivity.MIME_TYPE_IMAGE
                : SaveActivity.MIME_TYPE_VIDEO;
        createShareIntent(context, mime_type, file);

    }

    private static void createShareIntent(Context context, String type, File file) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_to_string));

        // Set the MIME type
        share.setType(type);

        Uri uri = Uri.fromFile(file);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));
    }

    public static void repost(Context context, File file) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        //set package
        share.setPackage("com.instagram.android");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //mime ish
        String mime_type = (file.getName().endsWith("png"))
                ? SaveActivity.MIME_TYPE_IMAGE
                : SaveActivity.MIME_TYPE_VIDEO;

        share.setType(mime_type);

        // Create the URI from the media
        Uri uri = Uri.fromFile(file);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(share);

    }
}
