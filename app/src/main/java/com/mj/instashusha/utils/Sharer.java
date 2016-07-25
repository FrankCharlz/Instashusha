package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.R;

import java.io.File;

/**
 * Created by Frank on 1/9/2016.
 *
 */

public class Sharer {


    private static final String PACKAGE_NONE = "";
    private static final String PACKAGE_WHATSAPP = "com.whatsapp";
    private static final String PACKAGE_INSTAGRAM = "com.instagram.android";


    public static void share(Context context, File file, boolean share_to_whatsapp, Tracker mTracker) {
        if (share_to_whatsapp) {
            createShareIntent(context, file, PACKAGE_WHATSAPP);
        } else {
            createShareIntent(context, file, PACKAGE_NONE);
        }

        track(mTracker);
    }

    private static void track(Tracker mTracker) {

        if (mTracker == null) return;

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("BUTTON CLICKS")
                .setAction("SHARE CLICKED")
                .setLabel("SHARE")
                .build());
    }


    public static void repost(Context context, File file, Tracker mTracker) {
        createShareIntent(context, file, PACKAGE_INSTAGRAM);
        track(mTracker);
    }

    private static void createShareIntent(Context context, File file, String app_package) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_to_string));


        shareIntent.setType(Media.getMimeType(file));

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

        if (app_package.equals(PACKAGE_INSTAGRAM)) {
            shareIntent.setPackage(PACKAGE_INSTAGRAM);
        } else if (app_package.equals(PACKAGE_WHATSAPP)) {
            shareIntent.setPackage(PACKAGE_WHATSAPP);
        }

        Intent chooserIntent = Intent.createChooser(shareIntent, "Share to: ");

        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //needed when starting activity without activity context

        context.startActivity(chooserIntent);

    }

}
