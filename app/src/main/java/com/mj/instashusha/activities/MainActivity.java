package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.Constants;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.services.ServiceMonitor;
import com.mj.instashusha.utils.Clip;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.OneTimeOps;
import com.mj.instashusha.utils.Prefs;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private boolean waking_from_pause = false;
    private View root_view;
    private DopeTextView btnViewDownloaded, btnOpenTutorial, btnOpenInstagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                OneTimeOps.checkAppIntroduction(context);
                OneTimeOps.getUserEmail(context);
            }
        }).start();


        initViews();
        programFlow();

        ServiceMonitor.checkAndStartBackgroundService(getBaseContext());
        loadAds();
        track();

    }

    private void track() {
        Tracker mTracker = ((InstagramApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_INSTRUCTION_FRAGMENT");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void loadAds() {
        final AdView mAdView = (AdView) findViewById(R.id.adview_main);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initViews() {

        root_view = findViewById(R.id.root_view_main_activity);

        btnOpenTutorial = (DopeTextView) findViewById(R.id.btn_maelezo_zaidi);
        btnOpenTutorial.setOnClickListener(new ButtonClicks());

        btnOpenInstagram = (DopeTextView) findViewById(R.id.btn_fungua_insta);
        btnOpenInstagram.setOnClickListener(new ButtonClicks());

        btnViewDownloaded = (DopeTextView) findViewById(R.id.btn_view_downloaded);
        btnViewDownloaded.setOnClickListener(new ButtonClicks());

        int idadi = Prefs.getNumberOfSavedMedia(context);
        btnViewDownloaded.setText(String.
                format(Locale.US,
                "%s (%d)", context.getResources().getString(R.string.view_downloaded), idadi));


    }


    private void programFlow() {
        //if back from save activity, then just  chill...
        InstagramApp.log("from save activity : " + InstagramApp.BACK_FROM_SAVE_ACTIVITY);
        if (InstagramApp.BACK_FROM_SAVE_ACTIVITY) {
            InstagramApp.BACK_FROM_SAVE_ACTIVITY = false;
            return;
        }

        String url = Clip.hasInstagramUrl(this);

        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            InstagramApp.log("No instagram url in clipboard");
            return;

        }

        //all conditions GOOD proceed to process instagram link
        //make it a good consistent format
        //test if it's the last downloaded url
        url = InstagramApp.processUrl(url);
        InstagramApp.log("URL is : " + url);
        if (Prefs.isTheLastUr(context, url)) {
            //no need to save this it again... it's already saved
            //showInstructionFragment();
        } else {
            //proceed to save activity
            proceedLoading(url);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //do not perform fragment transaction here..
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //method is guaranteed to be called after the Activity has been restored to its original state
        if (waking_from_pause) {
            //onresume is called after oncreate at first..
            programFlow();
            waking_from_pause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        waking_from_pause = true;
    }


    public void proceedLoading(final String url) {
        Intent intent = new Intent(context, SaveActivity.class);
        intent.putExtra(SaveActivity.SRC_URL, url);
        startActivity(intent);
        //finish();

    }


    class ButtonClicks implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_maelezo_zaidi:
                    startActivity(new Intent(context, IntroActivity.class));
                    break;

                case R.id.btn_fungua_insta:
                    try {
                        Intent instaIntent = context.getPackageManager()
                                .getLaunchIntentForPackage(Constants.INSTAGRAM_PACKAGE_NAME);
                        startActivity(instaIntent);
                    } catch (Exception e) {
                        InstagramApp.log(e.getLocalizedMessage());
                    }
                    break;

                case R.id.btn_view_downloaded:
                    int downloaded = InstagramApp.mediaDownloaded();
                    if (downloaded > 0) {
                        Intent t = new Intent(context, DownloadedActivity.class);
                        startActivity(t);
                    } else {
                        Snackbar
                                .make(root_view,
                                        "No media downloaded, download something first!",
                                        Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    break;
                default:
                    break;

            }
        }
    }

}
