package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mj.instashusha.R;

import java.io.File;

/**
 * Created by Frank on 1/9/2016.
 *
 *
 */

public class Sharer {

    public static void share(Context context, File file) {
        createShareIntent(context, file, false);
    }


    public static void repost(Context context, File file) {
        createShareIntent(context, file, true);
    }

    private static void createShareIntent(Context context, File file, boolean repost) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_to_string));


        shareIntent.setType(Media.getMimeType(file));

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        if (repost) {
            shareIntent.setPackage("com.instagram.android"); //repost to instagram...
        }

        Intent chooserIntent = Intent.createChooser(shareIntent, "Share to: ");

        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //needed when starting activity without activity context

        context.startActivity(chooserIntent);

    }

}
