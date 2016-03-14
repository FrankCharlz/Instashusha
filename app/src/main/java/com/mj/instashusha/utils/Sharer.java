package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mj.instashusha.R;
import com.mj.instashusha.activities.SaveActivity;

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

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_to_string));

        // Set the MIME type
        share.setType(Media.getMimeType(file));


        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        if (repost) {
            //set package
            share.setPackage("com.instagram.android");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to: "));

    }

}
