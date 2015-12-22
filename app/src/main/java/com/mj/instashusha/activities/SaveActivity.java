package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.codechimp.apprater.AppRater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;

public class SaveActivity extends AppCompatActivity {

    private static final float TOOLBAR_BG_ALPHA = 0.49f;
    private String media_type, image_url, video_url, source_url;
    private Context context;
    private ImageView imageView;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView tvToolbar;
    private LinearLayout buttonsContainer;

    private ProgressBar progressBar;

    private DopeTextView btnSave, btnShare, btnRepost;
    private String mime_type, save_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);


        AppRater.app_launched(this); //for ratings...

        context = this;
        initViews();

        Intent intent = getIntent();
        media_type = intent.getStringExtra(MainActivity.MEDIA_TYPE);
        image_url = intent.getStringExtra(MainActivity.IMAGE_URL);
        video_url = intent.getStringExtra(MainActivity.VIDEO_URL);
        source_url = intent.getStringExtra(MainActivity.SRC_URL);

        if (media_type.contains("video")) {
            mime_type = "video/*";
        } else {
            mime_type = "image/*";
        }

        Picasso.with(context).load(image_url).into(target);

        //money baby...
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();
        mAdView.loadAd(adRequest);

    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.image_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_container);

        toolbar = (Toolbar) findViewById(R.id.toolbar_save_activity);
        tvToolbar = (TextView) findViewById(R.id.tv_appname);

        ButtonClicks sl = new ButtonClicks();

        btnSave = (DopeTextView) findViewById(R.id.btn_download);
        btnSave.setOnClickListener(sl);

        btnShare = (DopeTextView) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(sl);

        btnRepost = (DopeTextView) findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(sl);

        buttonsContainer = (LinearLayout) findViewById(R.id.buttons_container);
        buttonsContainer.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_dl);
    }

    class ButtonClicks implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //puts the last url to shared prefs..
            Utils.setLastUrl(context, source_url);

            switch (view.getId()) {
                case R.id.btn_share:
                    download();
                    createShareIntent(mime_type, save_path);
                    break;

                case R.id.btn_repost:
                    download();
                    createInstagramIntent(mime_type, save_path);
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
        if (media_type.contains("video")) {
            save_path = InstagramApp.getAppVideoFolder() +Utils.getTimeStamp() + ".mp4";
            saveVideo(video_url, save_path);
        } else {
            save_path = InstagramApp.getAppPhotoFolder()+Utils.getTimeStamp()+".png";
            Utils.saveImage(context, imageView, save_path);
            openFinisherActivity();
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);

            buttonsContainer.setVisibility(View.VISIBLE);

            paletteThings(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void paletteThings(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                InstagramApp.log("Palette loaded");
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    int vbg = swatch.getRgb(); vbg = addAlphaToColor(vbg, TOOLBAR_BG_ALPHA);
                    int vtc = swatch.getTitleTextColor();

                    //frameLayout.setBackgroundColor(swatch.getRgb());

                    toolbar.setBackgroundColor(vbg);
                    buttonsContainer.setBackgroundColor(addAlphaToColor(vbg, 0.4f));
                    tvToolbar.setTextColor(vtc);
                }

                //progressBar.setProgressTintList(null);
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
                        publishProgress((int)(100.0 * downloaded/size));
                    }


                } finally {
                    if (in != null) in.close();
                    if (fout != null) fout.close();

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            InstagramApp.toast(getApplicationContext(), "Video saved at: " + save_path);
                            InstagramApp.log("Video saved at: " + save_path);
                            Utils.addFileToMediaDatabase(context, save_path);
                            progressBar.setVisibility(View.GONE);
                            openFinisherActivity();
                        }
                    });
                }

            }

        });
    }

    private void openFinisherActivity() {
        Intent intent = new Intent(context, FinisherActivity.class);
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
                tvToolbar.setText(""+progress);
            }
        });
    }


    private void createInstagramIntent(String type, String mediaPath){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        //set package
        share.setPackage("com.instagram.android");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_TEXT, "Repost by @InstaShusha");

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(share);
    }

    private void createShareIntent(String type, String mediaPath) {
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        share.putExtra(Intent.EXTRA_TEXT,"Shared from @InstaShusha");

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }


    public int addAlphaToColor(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}



