package com.mj.instashusha.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Patterns;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.activities.IntroActivity;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public static void getUserEmail(Context context) {


        if (!Prefs.hasPostedEmail(context)) {

            Pattern email_pattern = Patterns.EMAIL_ADDRESS;
            Account accounts[] = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (email_pattern.matcher(account.name).matches()) {
                    //got possible email..
                    sendEmailToServer(context, account.name);
                    InstagramApp.log(account.toString());
                    break; //I only need one email...

                }
            }
        }
    }

    private static void sendEmailToServer(final Context context, final String email) {

        RequestBody form = new FormBody.Builder()
                .add("email", email)
                .add("version", "" + getAppVersion(context))
                .build();

        Request request = new Request.Builder()
                .url("http://insta-dl.appspot.com/user/add")
                .post(form)
                .build();

        InstagramApp.log("Trying to send email address");

        InstagramApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Prefs.setSentEmailTrue(context);
                    InstagramApp.log("User email sent successfully");
                }
            }
        });


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
