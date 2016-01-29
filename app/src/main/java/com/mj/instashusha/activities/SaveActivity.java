package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
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
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.DopeTextView;
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

    private DopeTextView btnSave;
    private DopeTextView btnRepost;
    private boolean isImage;
    private View containerActivitySave;
    private boolean share_after_download = false,
            repost_after_download = false;

    private static final String BASE_URL = "http://insta-dl.appspot.com/dl?source=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        //money baby...
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AppRater.app_launched(this); //for ratings...

        context = this;
        initViews();
        animateLoading();

        Intent intent = getIntent();
        source_url = intent.getStringExtra(MainActivity.SRC_URL);

        proceed(source_url);

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

                        //Setting mime type...
                        //String mime_type = isImage ? MIME_TYPE_IMAGE : MIME_TYPE_VIDEO;

                        Picasso.with(context).load(image_url).into(picasso_target);
                    }

                    @Override
                    public void onVideoResponse(InputStream stream, long size) throws IOException {}

                });
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.image_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        textViewToolbar = (TextView) findViewById(R.id.tv_appname);

        ButtonClicks sl = new ButtonClicks();

        btnSave = (DopeTextView) findViewById(R.id.btn_download);
        btnSave.setOnClickListener(sl);

        DopeTextView btnShare = (DopeTextView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(sl);

        btnRepost = (DopeTextView) findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(sl);

        buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);
        buttonsContainer.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_dl);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        containerActivitySave = findViewById(R.id.container_layout_activity_save);

    }

    private void animateLoading() {
        //TextView subiri = (TextView) findViewById(R.id.tv_subiri);
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
                            .make(containerActivitySave,
                                    "Wait while the video is being downloaded.\n" +
                                            "It will be shared thereafter",
                                    Snackbar.LENGTH_LONG)
                            .show();
                    break;

                case R.id.btn_repost:
                    repost_after_download = true;
                    download();
                    Snackbar
                            .make(containerActivitySave,
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
        //puts the last url to shared prefs..
        Utils.setLastUrl(context, source_url);

        //sets the save path, so that it can be used in share intent...
        //ugly though
        String save_path;
        if (!isImage) {
            save_path = InstagramApp.VIDEO_FOLDER_PATH + "/" + Utils.getTimeStamp() + getVidExtension();
            saveVideo(video_url, save_path);
        } else {
            save_path = InstagramApp.PHOTO_FOLDER_PATH + "/" + Utils.getTimeStamp() + ".png";
            Utils.saveImage(context, imageView, save_path);

            if (repost_after_download) {
                Sharer.repost(context, new File(save_path));
                repost_after_download = false;
            } else if (share_after_download) {
                Sharer.share(context, new File(save_path));
                share_after_download = false;
            } else {
                openFinisherActivity();
            }
        }
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
                    buttonsContainer.setBackgroundColor(Utils.addAlphaToColor(vbg, 0.41f));
                    textViewToolbar.setTextColor(vtc);
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
            public void onVideoResponse(InputStream stream, long size) throws IOException {

                BufferedInputStream in = null;
                FileOutputStream fout = null;
                long downloaded = 0;
                try {
                    in = new BufferedInputStream(stream);
                    fout = new FileOutputStream(save_path);


                    final byte data[] = new byte[1024];
                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        downloaded += count;
                        fout.write(data, 0, count);
                        publishProgress((int) (100.0 * downloaded / size));
                    }

                    InstagramApp.log("Video saved at: " + save_path);

                } finally {
                    if (in != null) in.close();
                    if (fout != null) fout.close();

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            if (share_after_download) {
                                Sharer.share(context, new File(save_path));
                                share_after_download = false;
                            } else if (repost_after_download) {
                                Sharer.repost(context, new File(save_path));
                                repost_after_download = false;
                            } else {
                                openFinisherActivity();
                            }
                            Utils.addFileToMediaDatabase(context, save_path);
                        }
                    });
                }

            }

        });
    }

    private void openFinisherActivity() {
        Intent intent = new Intent(context, DownloadedActivity.class);
        startActivity(intent);
        finish();
    }


    private void publishProgress(final int progress) {
        //InstagramApp.log("Progress : "+v);
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                textViewToolbar.setText("" + progress + "%");
            }
        });
    }



}