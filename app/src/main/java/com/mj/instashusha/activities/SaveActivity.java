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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.Constants;
import com.mj.instashusha.MyApp;
import com.mj.instashusha.R;
import com.mj.instashusha.network.Downloader;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.network.ProgressListener;
import com.mj.instashusha.utils.Prefs;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.codechimp.apprater.AppRater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;

public class SaveActivity extends AppCompatActivity {

    /**
     * TODO: instagram aspect ratios are; up to 1.91:1 for landscape and 4:5 for portrait. do something.
     */

    public static final String FROM_SERVICE = "0x00123df3";
    public static final String SRC_URL = "0x025671f";
    private static final float TOOLBAR_BG_ALPHA = 0.49f;
    private static final float BUTTONS_BG_ALPHA = 0.36f;
    private String image_url, video_url, source_url;
    private Context context;
    private ImageView imageView;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private View buttonsContainer;

    private NumberProgressBar progressLine;

    private View btnSave;
    private View btnShare;
    private boolean isImage;
    private View activity_view_container;
    private boolean share_after_download = false, repost_after_download = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean from_service;
    private ButtonClicks buttonClicks;
    private boolean share_to_whatsapp = false;
    private Tracker mTracker;


    private Target picasso_target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            paletteColors(bitmap);
            findViewById(R.id.lyt_subiri_kidogo).setVisibility(View.GONE);
            buttonsContainer.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Snackbar.make(
                    activity_view_container,
                    "Bitmap failed to load, sorry try again...",
                    Snackbar.LENGTH_INDEFINITE)
                    .show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onDestroy() {
        //Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Debug.startMethodTracing("ishusha");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        context = this;
        AppRater.app_launched(this); //for ratings...

        Intent intent = getIntent();

        String action = intent.getAction();
        String type = intent.getType();

        source_url = intent.getStringExtra(SRC_URL);
        from_service = intent.getBooleanExtra(FROM_SERVICE, false);

        if (action != null
                && type != null
                && action.equalsIgnoreCase(Intent.ACTION_SEND)
                && type.equals("text/plain")) {
            proceedFromShare(intent.getStringExtra(Intent.EXTRA_TEXT));
        }

        initViews();
        proceed(source_url);

        loadAds();
        track();

    }

    private void proceedFromShare(String shared_text) {
        MyApp.log("from intent : " + shared_text);

        int start = shared_text.indexOf("https://www.instagram.com");
        if (start >= 0) {
            source_url = shared_text.substring(start).trim();
        } else {
            MyApp.log("Url not available in shared text");
            Snackbar
                    .make(activity_view_container,
                            "Url is not available in shared data, sorry!",
                            Snackbar.LENGTH_INDEFINITE)
                    .show();

        }

    }

    private void track() {
        //// TODO: 7/21/2016 make tracker single
        mTracker = ((MyApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_SAVE_ACTIVITY");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void proceed(String source_url) {
        final Request request = new Request.Builder()
                .url(Constants.BASE_URL + source_url)
                .build();

        MyApp.log("Request built: for url");

        MyApp.getOkHttpClient().newCall(request).enqueue(
                new HttpCallback() {
                    @Override
                    public void onUrlResponse(InstaResponse response) {
                        image_url = response.image_url;
                        video_url = response.video_url;
                        isImage = video_url.isEmpty();

                        //load the thumb image into image-view
                        Picasso.with(context).load(image_url).into(picasso_target);
                    }

                    @Override
                    public void onVideoResponse(InputStream stream, long size) throws IOException {
                    }

                });
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setOnTouchListener(new ImageTouched());

        toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        textViewToolbar = (TextView) findViewById(R.id.tv_appname);

        buttonClicks = new ButtonClicks();

        if (from_service) {
            initButtons2();
        } else {
            initButtons1();
        }


        buttonsContainer.setVisibility(View.GONE);

        progressLine = (NumberProgressBar) findViewById(R.id.number_progress_bar);

        activity_view_container = findViewById(R.id.container_layout_activity_save);


        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        findViewById(R.id.tv_subiri).startAnimation(rotateAnimation);
    }

    private void initButtons2() {
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setLayoutResource(R.layout.layout_buttons_share);
        buttonsContainer = stub.inflate();

        btnSave = findViewById(R.id.btn_download_2);
        btnSave.setOnClickListener(buttonClicks);

        btnShare = findViewById(R.id.btn_share_2);
        btnShare.setOnClickListener(buttonClicks);
    }

    private void initButtons1() {
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setLayoutResource(R.layout.layout_buttons);
        buttonsContainer = stub.inflate();

        btnSave = findViewById(R.id.btn_download);
        btnSave.setOnClickListener(buttonClicks);

        btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(buttonClicks);

        View btnRepost = findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(buttonClicks);
    }

    private String getExtension(String url) {
        //takes in file url, returns last six chars including extension
        int urf = url.length();
        if (urf > 6) return url.substring(urf - 6);
        return ""; //returns empty string on FUS
    }

    private void showDownloadingWarnSnackBar() {
        Snackbar
                .make(activity_view_container,
                        "Wait while the video is being downloaded.\n" +
                                "It will be shared thereafter",
                        Snackbar.LENGTH_LONG)
                .show();
    }


    private void download() {
        //sets the save path, so that it can be used in share intent...
        //ugly though
        String save_path;
        if (!isImage) {
            MyApp.log("downloading video started");
            save_path = MyApp.getAppFolder().getAbsolutePath() + File.separator + Utils.getTimeStamp() + getExtension(video_url);
            saveVideo(video_url, save_path);
            //do after saving will go into above method.
        } else {
            MyApp.log("downloading image started");
            save_path = MyApp.getAppFolder().getAbsolutePath() + File.separator + Utils.getTimeStamp() + getExtension(image_url);
            Utils.saveImage(imageView, save_path);
            doAfterSaving(save_path);
        }
        //puts the last url to shared prefs..
        Prefs.setLastUrl(context, source_url);
    }

    private void paletteColors(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                MyApp.log("Palette loaded");
                int bar_color = Utils.addAlphaToColor(Color.DKGRAY, 0.220f);
                int btns_color = Utils.addAlphaToColor(bar_color, TOOLBAR_BG_ALPHA * BUTTONS_BG_ALPHA);
                int text_color = Color.BLACK;

                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    bar_color = swatch.getRgb();
                    bar_color = Utils.addAlphaToColor(bar_color, TOOLBAR_BG_ALPHA);
                    btns_color = Utils.addAlphaToColor(bar_color, TOOLBAR_BG_ALPHA * BUTTONS_BG_ALPHA);
                    text_color = swatch.getTitleTextColor();
                }

                toolbar.setBackgroundColor(bar_color);
                buttonsContainer.setBackgroundColor(btns_color);
                textViewToolbar.setTextColor(text_color);

                progressLine.setReachedBarColor(bar_color);
                progressLine.setUnreachedBarColor(btns_color);
                progressLine.setProgressTextColor(Color.DKGRAY);

            }
        });
    }

    private void saveVideo(String video_url, final String save_path) {
        progressLine.setVisibility(View.VISIBLE);
        MyApp.getOkHttpClient()
                .newCall(new Request.Builder()
                        .url(video_url)
                        .build()).enqueue(new HttpCallback() {
            @Override
            public void onUrlResponse(InstaResponse ir) {
            }

            @Override
            public void onVideoResponse(InputStream stream, final long size) throws IOException {
                Downloader.saveStream(stream, size, save_path, new VideoDownloadListener());
            }

        });
    }

    private void doAfterSaving(String save_path) {
        /***
         * share if is it
         * add the media to gallery
         */
        //// TODO: 5/21/2016 tracking removed below
        Utils.addFileToMediaDatabase(context, save_path);
        Prefs.incrementDownloadedMedia(context);
        if (share_after_download) {
            Sharer.share(context, new File(save_path), share_to_whatsapp, mTracker);
            share_after_download = false;
            share_to_whatsapp = false;
        } else if (repost_after_download) {
            Sharer.repost(context, new File(save_path), mTracker);
            repost_after_download = false;
        } else {
            //open downloaded activity....
            Intent intent = new Intent(context, DownloadedActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        /***
         * since previous activity (MainActivity) checks last url, if not equal it goes to
         * InstructionsFragment it wise to set this to last downloaded after a user clicks back
         * to prevent reloading the thumbnail cycle..
         * OR just use the alternative below:
         */
        super.onBackPressed();
        MyApp.BACK_FROM_SAVE_ACTIVITY = true;
        finish();
    }

    class ButtonClicks implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_share:
                    share_after_download = true;
                    download();
                    showDownloadingWarnSnackBar();
                    break;

                case R.id.btn_share_2:
                    share_after_download = true;
                    share_to_whatsapp = true;
                    download();
                    showDownloadingWarnSnackBar();
                    break;

                case R.id.btn_repost:
                    repost_after_download = true;
                    download();
                    showDownloadingWarnSnackBar();
                    break;

                case R.id.btn_download:
                case R.id.btn_download_2:
                    MyApp.log("clicked : " + view.toString());
                    download();
                    break;
                default:
                    break;
            }

        }
    }

    private class VideoDownloadListener implements ProgressListener {

        @Override
        public void onStart() {
        }

        @Override
        public void onProgress(final int progress) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressLine.setProgress(progress);
                }
            });
        }

        @Override
        public void onCompleted(final String save_path) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressLine.setVisibility(View.GONE);
                    doAfterSaving(save_path);
                }
            });
        }
    }


    private class ImageTouched implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //view = (View) view.getParent();
            int og_top = view.getTop();
            float startY = 0;
            float dy = 0, move = 0;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    //MyApp.log(" start : "+startY);
                    //MyApp.log(" current : "+event.getRawY());
                    dy = event.getRawY() - startY;
                    dy/=8;
                    if (dy > 150) dy = 150;

                    MyApp.log(" moved : "+dy);
                    view.animate()
                            .y(dy)
                            .setDuration(0)
                            .start();

                    buttonsContainer
                            .animate()
                            //.y(dy)
                            .alpha(1 - dy/150)
                            .setDuration(0)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                    MyApp.log(" up : "+event.getRawY());
                    view.animate()
                            .y(og_top)
                            .setDuration(93)
                            .start();

                    buttonsContainer
                            .animate()
                            .alpha(1)
                            .setDuration(93)
                            .start();
                    break;

                default: break;
            }

            return true;
        }
    }
}