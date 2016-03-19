package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.Constants;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.MenuClick;
import com.mj.instashusha.utils.Sharer;
import com.mj.instashusha.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.codechimp.apprater.AppRater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;

public class SaveActivity extends AppCompatActivity {

    /**
     * TODO: instagram aspect ratios are; up to 1.91:1 for landscape and 4:5 for portrait. do something.
     */


    public static final String BASE_URL = "http://insta-dl.appspot.com/dl?source=";
    private static final float TOOLBAR_BG_ALPHA = 0.49f;
    public static final String FROM_SERVICE = "0x00123df3";
    public static final String SRC_URL = "0x025671f";
    private String image_url, video_url, source_url;
    private Context context;
    private ImageView imageView;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private View buttonsContainer;

    private ProgressBar progressBar;

    private View btnSave, btnRepost, btnShare;
    private boolean isImage;
    private View activity_view_container;
    private boolean share_after_download = false, repost_after_download = false;

    private  Handler handler = new Handler(Looper.getMainLooper());
    private int percent = 0;
    private boolean from_service;
    private ButtonClicks buttonClicks;
    private boolean share_to_whatsapp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        context = this;
        AppRater.app_launched(this); //for ratings...

        //loadAds();

        Tracker mTracker = ((InstagramApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_SAVE_ACTIVITY");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        Intent intent = getIntent();
        source_url = intent.getStringExtra(SRC_URL);
        from_service = intent.getBooleanExtra(FROM_SERVICE, false);

        //from_service = new Random().nextBoolean(); //for testing purpose...

        initViews();
        proceed(source_url);

    }



    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
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

        buttonClicks = new ButtonClicks();

        if (from_service) {initButtons2(); } else  {initButtons1();}


        buttonsContainer.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_dl);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        activity_view_container = findViewById(R.id.container_layout_activity_save);

        View ta = findViewById(R.id.tigo_ad_save_activity);
        ta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open browser...
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.TIGO_URL));
                context.startActivity(intent);
            }
        });

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        findViewById(R.id.tv_subiri).startAnimation(rotateAnimation);
    }

    private void initButtons2() {
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        stub.setLayoutResource(R.layout.layout_buttons_share);
        buttonsContainer = stub.inflate();

        btnSave =findViewById(R.id.btn_download_2);
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

        btnRepost = findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(buttonClicks);
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
                    showDownloadingWarnSnackBar();

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
                    InstagramApp.log("clicked : "+view.toString());
                    download();
                    break;
                default:
                    break;
            }

        }
    }

    private void showDownloadingWarnSnackBar() {
        Snackbar
                .make(activity_view_container,
                        "Wait while the video is being downloaded.\n" +
                                "It will be reposted thereafter",
                        Snackbar.LENGTH_LONG)
                .show();
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
            doAfterSaving(save_path);
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
            Snackbar.make(activity_view_container, "Sorry, try again later...", Snackbar.LENGTH_INDEFINITE)
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
                            doAfterSaving(save_path);
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

    private void doAfterSaving(String save_path) {
        if (share_after_download) {
            Sharer.share(context, new File(save_path), share_to_whatsapp);
            share_after_download = false;
            share_to_whatsapp  = false;
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