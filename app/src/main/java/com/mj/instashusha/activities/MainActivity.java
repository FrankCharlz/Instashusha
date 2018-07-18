package com.mj.instashusha.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mj.instashusha.Constants;
import com.mj.instashusha.MyApp;
import com.mj.instashusha.R;
import com.mj.instashusha.services.ServiceMonitor;
import com.mj.instashusha.utils.Clip;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.OneTimeOps;
import com.mj.instashusha.utils.Prefs;
import com.mj.instashusha.utils.Utils;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 2;
    private Context context;
    private boolean waking_from_pause = false;
    private View root_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Prefs.isFirstLaunch(context)) {
            startActivity(new Intent(context, IntroActivity.class));
        }

        context = this;

        doPermissions();


        initViews();
        programFlow();

        ServiceMonitor.checkAndStartBackgroundService(getBaseContext());
        track();

    }

    private void doPermissions() {

        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean hasAllPermissions = true;

        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                MyApp.log("permission not granted: " + perm);
                hasAllPermissions = false;
                break;
            }
        }

        if (!hasAllPermissions) {
            //just ask for all the permissions, even if only one is missing
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    permissions,
                    MY_PERMISSIONS_REQUEST
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    MyApp.log("permissions str: "+ Arrays.toString(permissions));
                    MyApp.log("permissions: "+ Arrays.toString(grantResults));

                } else {
                    // not giving shit any more
                    Toast.makeText(this, "App may mis-behave due to limited permissions. Please allow app to access" +
                            " your storage either through settings or by refreshing. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void track() {
//        Tracker mTracker = ((MyApp) getApplication()).getDefaultTracker();
//        mTracker.enableAdvertisingIdCollection(true);
//        mTracker.setScreenName("SCREEN_INSTRUCTION_FRAGMENT");
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initViews() {

        root_view = findViewById(R.id.root_view_main_activity);

        final ButtonClicks buttonClicks = new ButtonClicks();
        final DopeTextView btnViewDownloaded, btnOpenTutorial, btnOpenInstagram;

        btnOpenTutorial = (DopeTextView) findViewById(R.id.container_help);
        btnOpenTutorial.setOnClickListener(buttonClicks);

        btnOpenInstagram = (DopeTextView) findViewById(R.id.container_start_instagram);
        btnOpenInstagram.setOnClickListener(buttonClicks);

        btnViewDownloaded = (DopeTextView) findViewById(R.id.container_downloaded);
        btnViewDownloaded.setText(String.format(Locale.CANADA, "%s (%d)", btnViewDownloaded.getText(), Utils.numberOfDownloaded()));
        btnViewDownloaded.setOnClickListener(buttonClicks);

        ImageView logo = (ImageView) findViewById(R.id.logo_main_activity);

    }


    private void programFlow() {
        //if back from save activity, then just  chill...
        MyApp.log("from save activity : " + MyApp.BACK_FROM_SAVE_ACTIVITY);
        if (MyApp.BACK_FROM_SAVE_ACTIVITY) {
            MyApp.BACK_FROM_SAVE_ACTIVITY = false;
            return;
        }

        String url = Clip.hasInstagramUrl(this);

        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            MyApp.log("No instagram url in clipboard");
            return;

        }

        //all conditions GOOD proceed to process instagram link
        //make it a good consistent format
        //test if it's the last downloaded url
        url = Utils.processUrl(url);
        MyApp.log("URL is : " + url);
        if (!Prefs.isTheLastUr(context, url)) {
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
            MyApp.log("something clicked : "+view.getId());
            switch (view.getId()) {
                case R.id.container_help:
                    startActivity(new Intent(context, IntroActivity.class));
                    break;

                case R.id.container_start_instagram:
                    try {
                        Intent instaIntent = context.getPackageManager()
                                .getLaunchIntentForPackage(Constants.INSTAGRAM_PACKAGE_NAME);
                        startActivity(instaIntent);
                    } catch (Exception e) {
                        MyApp.log(e.getLocalizedMessage());
                        Snackbar
                                .make(root_view,
                                        "Could not open Instagram App. Make sure it is installed!",
                                        Snackbar.LENGTH_LONG)
                                .show();
                    }
                    break;

                case R.id.container_downloaded:
                    int downloaded = Utils.numberOfDownloaded();
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
