package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mj.instashusha.activities.IntroActivity;

/**
 * Created by Frank on 3/13/2016.
 * All methods here should execute in a different thread..
 */
public class OneTimeOps {

    public static void checkAppIntroduction(Context context) {

        if (Prefs.isFirstLaunch(context)) {
            Intent i = new Intent(context, IntroActivity.class);
            context.startActivity(i);
        }
    }

    public static int getAppVersion(Context context) {
        int version = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}
