package com.mj.instashusha.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
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
    private static final String MIME_TYPE_VIDEO = "video/*";
    private static final String MIME_TYPE_IMAGE = "image/*";
    private String image_url, video_url, source_url;
    private Context context;
    private ImageView imageView;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private LinearLayout buttonsContainer;

    private ProgressBar progressBar;

    private DopeTextView btnSave;
    private DopeTextView btnRepost;
    private String mime_type, save_path;
    private boolean isImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        //money baby...
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_save);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        // AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AppRater.app_launched(this); //for ratings...

        context = this;
        initViews();

        Intent intent = getIntent();
        InstaResponse response = (InstaResponse) intent.getSerializableExtra(InstaResponse.SERIALIZE);

        image_url = response.image_url;
        video_url = response.video_url;
        source_url = intent.getStringExtra(MainActivity.SRC_URL);

        isImage = video_url.isEmpty();

        //Setting mime type...
        mime_type = isImage ? MIME_TYPE_IMAGE :  MIME_TYPE_VIDEO;

        Picasso.with(context).load(image_url).into(picasso_target);

        //start to download video here to enhance user experience...
        if (!isImage) downloadInBackground();

    }

    private void downloadInBackground() {

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

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings);
        btnMenu.setOnClickListener(new MenuClick(this));

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
                    if (isImage) {
                        download();
                        createShareIntent(mime_type, save_path);
                    } else {
                        InstagramApp.toast(context, "Sorry...\nSAVE kwanza ndo ushee.");
                    }
                    break;

                case R.id.btn_repost:
                    if (isImage) {
                        download();
                        createInstagramIntent(mime_type, save_path);
                    } else {
                        InstagramApp.toast(context, "Sorry...\nSAVE kwanza ndo urepost");
                    }
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
        if (!isImage) {
            save_path = InstagramApp.VIDEO_FOLDER_PATH + "/" + Utils.getTimeStamp() + getVidExtension();
            saveVideo(video_url, save_path);
        } else {
            save_path = InstagramApp.PHOTO_FOLDER_PATH + "/" + Utils.getTimeStamp() + ".png";
            Utils.saveImage(context, imageView, save_path);
            openFinisherActivity();
        }
    }

    private Target picasso_target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
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
                textViewToolbar.setText(progress);
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


}