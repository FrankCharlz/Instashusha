package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.MenuClick;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.Utils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.codechimp.apprater.AppRater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SaveActivity extends AppCompatActivity {

    /**
     * TODO: instagram aspect ratios are; up to 1.91:1 for landscape and 4:5 for portrait.
     */


    public static final String BASE_URL = "http://insta-dl.appspot.com/dl?source=";
    private static final float TOOLBAR_BG_ALPHA = 0.49f;
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    private String image_url, video_url, source_url;
    private Context context;
    private ImageView imageView;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private LinearLayout buttonsContainer;

    private ProgressBar progressBar;

    private DopeTextView btnSave, btnRepost;
    private boolean isImage;
    private View activity_view_container;
    private boolean share_after_download = false,
            repost_after_download = false;

    private  Handler handler = new Handler(Looper.getMainLooper());
    private int percent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        //loadAds();

        AppRater.app_launched(this); //for ratings...

        Tracker mTracker = ((InstagramApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_SAVE_ACTIVITY");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        context = this;
        initViews();
        animateLoading();

        Intent intent = getIntent();
        source_url = intent.getStringExtra(MainActivity.SRC_URL);
        proceed(source_url);

    }

    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    private void proceed(String source_url) {
        final Request request = new Request.Builder()
                .url(BASE_URL + source_url)
                .build();

        InstagramApp.log("Request built: for url");

        InstagramApp.getOkHttpClient().newCall(request).enqueue(
                new HttpCallback() {
                    @Override
                    public void onUrlResponse(InstaResponse response) {
                        image_url = response.image_url;
                        video_url = response.video_url;
                        isImage = video_url.isEmpty();

                        Picasso.with(context).load(image_url).into(picasso_target);
                    }

                    @Override
                    public void onVideoResponse(InputStream stream, long size) throws IOException {
                    }

                });
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.image_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        textViewToolbar = (TextView) findViewById(R.id.tv_appname);

        ButtonClicks buttonClicks = new ButtonClicks();

        btnSave = (DopeTextView) findViewById(R.id.btn_download);
        btnSave.setOnClickListener(buttonClicks);

        DopeTextView btnShare = (DopeTextView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(buttonClicks);

        btnRepost = (DopeTextView) findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(buttonClicks);

        buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);
        buttonsContainer.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_dl);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        activity_view_container = findViewById(R.id.container_layout_activity_save);

    }

    private void animateLoading() {
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        findViewById(R.id.tv_subiri).startAnimation(rotateAnimation);
    }

    public String getVidExtension() {
        int urf = source_url.length();
        return source_url.substring(urf-4, urf);
    }

    class ButtonClicks implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_share:
                    share_after_download = true;
                    download();
                    Snackbar
                            .make(activity_view_container,
                                    "Wait while the video is being downloaded.\n" +
                                            "It will be shared thereafter",
                                    Snackbar.LENGTH_LONG)
                            .show();
                    break;

                case R.id.btn_repost:
                    repost_after_download = true;
                    download();
                    Snackbar
                            .make(activity_view_container,
                                    "Wait while the video is being downloaded.\n" +
                                            "It will be reposted thereafter",
                                    Snackbar.LENGTH_LONG)
                            .show();
                    break;

                case R.id.btn_download:
                    download();
                    break;
                default: break;
            }

        }
    }

    private void download() {
        //sets the save path, so that it can be used in share intent...
        //ugly though
        String save_path;
        if (!isImage) {
            save_path = InstagramApp.getAppFolder().getAbsolutePath()+File.separator+Utils.getTimeStamp() + getVidExtension();
            saveVideo(video_url, save_path);
        } else {
            save_path = InstagramApp.getAppFolder().getAbsolutePath()+File.separator+Utils.getTimeStamp() + ".png";
            Utils.saveImage(context, imageView, save_path);
            afterDownloading(save_path);
        }
        //puts the last url to shared prefs..
        Utils.setLastUrl(context, source_url);
    }

    private Target picasso_target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            findViewById(R.id.lyt_subiri_kidogo).setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
            buttonsContainer.setVisibility(View.VISIBLE);
            paletteColors(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Snackbar.make(activity_view_container, "Imegoma, tafadhali jaribu tena", Snackbar.LENGTH_INDEFINITE)
                    .show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void paletteColors(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                InstagramApp.log("Palette loaded");
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    int vbg = swatch.getRgb();
                    vbg = Utils.addAlphaToColor(vbg, TOOLBAR_BG_ALPHA);
                    int vtc = swatch.getTitleTextColor();

                    toolbar.setBackgroundColor(vbg);
                    buttonsContainer.setBackgroundColor(Utils.addAlphaToColor(vbg, 0.425f));
                    textViewToolbar.setTextColor(vtc);
                } else {
                    toolbar.setBackgroundColor(Utils.addAlphaToColor(Color.DKGRAY, 0.220f));
                    buttonsContainer.setBackgroundColor(Utils.addAlphaToColor(Color.DKGRAY, 0.084f));
                }

            }
        });
    }

    private void saveVideo(String video_url, final String save_path) {
        progressBar.setVisibility(View.VISIBLE);
        InstagramApp.getOkHttpClient()
                .newCall(new Request.Builder()
                        .url(video_url)
                        .build()).enqueue(new HttpCallback() {
            @Override
            public void onUrlResponse(InstaResponse ir) {
            }

            @Override
            public void onVideoResponse(InputStream stream, final long size) throws IOException {

                BufferedInputStream in = null;
                FileOutputStream fout = null;
                long downloaded = 0;
                try {
                    in = new BufferedInputStream(stream);
                    fout = new FileOutputStream(save_path);

                    int buffer_size = 1024 * 64;
                    final byte data[] = new byte[buffer_size];
                    int count;

                    handler.post(progressRunnable);

                    while ((count = in.read(data, 0, buffer_size)) != -1) {
                        downloaded += count;
                        fout.write(data, 0, count);
                        percent = (int) (100.0f * downloaded / size);
                    }

                } finally {
                    if (in != null) in.close();
                    if (fout != null) fout.close();
                    handler.removeCallbacks(progressRunnable);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            afterDownloading(save_path);
                        }
                    });
                }

            }

        });
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            publishProgress(percent);
        }
    };

    private void publishProgress(int percent) {
        progressBar.setProgress(percent);
        textViewToolbar.setText("" + percent + "%");
        handler.postDelayed(progressRunnable, 600);
    }

    private void afterDownloading(String save_path) {
        if (share_after_download) {
            Sharer.share(context, new File(save_path));
            share_after_download = false;
        } else if (repost_after_download) {
            Sharer.repost(context, new File(save_path));
            repost_after_download = false;
        } else {
            //open downloaded activity....
            Intent intent = new Intent(context, DownloadedActivity.class);
            startActivity(intent);
            finish();
        }
        Utils.addFileToMediaDatabase(context, save_path);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /***
         * since previous activity (MainActivity) checks last url if equals.. it goes to
         * InstructionsFragment it wise to set this to last downloaded after a user clicks back
         *
         * OR just use the alternative below:
         */
        InstagramApp.BACK_FROM_SAVE_ACTIVITY = true;
        finish();
    }

}