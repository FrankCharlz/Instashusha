package com.mj.instashusha.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.fragments.InstructionFragment;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.Utils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://insta-dl.appspot.com/dl?source=";
    public static final String SRC_URL = "UJYJHjy";

    private Context context;
    private ProgressDialog progressBar;
    private boolean activityWasPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide home icon.. Because it not used here in this activity..
        findViewById(R.id.toolbar_action_settings_home).setVisibility(View.GONE);

        context = this;
        InstagramApp.makeAppFolder();
        checkAppIntroduction();

        Intent i = getIntent();
        if (i.getBooleanExtra(InstagramApp.GO_TO_INSTRUCTIONS, false)) {
            showInstructionFragment();
            return;
        }

        programFlow();

    }

    private void programFlow() {
        String url = InstagramApp.getLinkFromClipBoard(this);
        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            InstagramApp.log("No instagram url in clipboard");
            showInstructionFragment();

        } else {
            //process instagram link
            InstagramApp.log("URL is : " + url);

            boolean isLastUrl = Utils.isTheLastUr(context, url);
            if(isLastUrl) {
                //no need to download it again...
                showInstructionFragment();
            } else {
                proceedLoading(url);
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activityWasPaused) {
            //onresume is called after oncreate at first..
            programFlow();
            activityWasPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityWasPaused = true;
    }

    private void showInstructionFragment() {
        InstructionFragment fragment = new InstructionFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    private void checkAppIntroduction() {
        //using thread to read prefs...
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {

                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    getPrefs.edit().putBoolean("firstStart", false).apply();
                }
            }
        });
        t.start();

    }

    public void proceedLoading(final String url) {

        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage(getResources().getString(R.string.message_when_loading));
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressBar.setContentView(R.layout.activity_save);
        progressBar.show();


        final Request request = new Request.Builder()
                .url(BASE_URL + url)
                .build();

        InstagramApp.log("Request built: for url");

        InstagramApp.getOkHttpClient().newCall(request).enqueue(
                new HttpCallback() {
                    @Override
                    public void onUrlResponse(InstaResponse ir) {
                        InstagramApp.log(ir.toString());
                        Intent intent = new Intent(context, SaveActivity.class);
                        intent.putExtra(InstaResponse.SERIALIZE, ir);
                        intent.putExtra(SRC_URL, url);
                        progressBar.dismiss();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onVideoResponse(InputStream stream, long size) throws IOException {}

                });
    }

}
