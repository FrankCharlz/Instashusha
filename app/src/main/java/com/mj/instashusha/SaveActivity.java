package com.mj.instashusha;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class SaveActivity extends AppCompatActivity {

    private static final float TOOLBAR_BG_ALPHA = 0.49f;
    private String media_type, image_url, video_url;
    private Context context;
    private ImageView imageView;
    private OkHttpClient client;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private TextView tvToolbar;

    private Button btnSave, btnShare, btnRepost;
    private String mime_type, shared_file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        context = this;

        initViews();

    /*
        Intent intent = getIntent();
        media_type = intent.getStringExtra(MainActivity.MEDIA_TYPE);
        image_url = intent.getStringExtra(MainActivity.IMAGE_URL);
        video_url = intent.getStringExtra(MainActivity.VIDEO_URL);
        */

        int ds[] = {
                R.drawable.aaaaa,
                R.drawable.bbbbb,
                R.drawable.ccccc,
                R.drawable.eeee,
                R.drawable.fff,
                R.drawable.ggg,
                R.drawable.dddd,
                R.drawable.hhh,
                R.drawable.iiii,
                R.drawable.jjjj
        };

        Picasso.with(context).load(ds[new Random().nextInt(ds.length)]).into(target);

        //Picasso.with(context).load(image_url).into(target);


    }

    private Drawable getD(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id, context.getTheme());
        } else {
            return getResources().getDrawable(id);
        }
    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.image_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout_container);

        toolbar = (Toolbar) findViewById(R.id.toolbar_save_activity);
        tvToolbar = (TextView) findViewById(R.id.tv_appname);

        ShareListener sl = new ShareListener();

        btnSave = (Button) findViewById(R.id.btn_download);
        btnSave.setOnClickListener(new ButtonListener());

        btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(sl);

        btnRepost = (Button) findViewById(R.id.btn_repost);
        btnRepost.setOnClickListener(sl);

        btnSave.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);
        btnRepost.setVisibility(View.GONE);
    }

    class ShareListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_share :
                    createShareIntent(mime_type, shared_file_path);
                    break;

                case R.id.btn_repost:
                    createInstagramIntent(mime_type, shared_file_path);
                    break;

                default: break;
            }

        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);

            btnSave.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            btnRepost.setVisibility(View.VISIBLE);

            doPalleteColourings(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void doPalleteColourings(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                InstagramApp.log("Palette loaded");
                Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                if (swatch != null) {
                    int vbg = swatch.getRgb(); vbg = addAlphaToColor(vbg, TOOLBAR_BG_ALPHA);
                    int vtc = swatch.getTitleTextColor();

                    //frameLayout.setBackgroundColor(swatch.getRgb());

                    toolbar.setBackgroundColor(vbg);
                    tvToolbar.setTextColor(vtc);
                }

            }
        });
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            final String save_path;

            if (media_type.contains("video")) {
                save_path = InstagramApp.getAppVideoFolder() + getTimeStamp() + ".mp4";
                saveVideo(video_url, save_path);
            } else {
                save_path = InstagramApp.getAppPhotoFolder()+getTimeStamp()+".png";
                saveImage(imageView, save_path);
            }

            InstagramApp.log(save_path);

        }
    }

    private void saveImage(ImageView imageView, String save_path) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();

        File save_file = new File(save_path);
        try {
            boolean fcs = save_file.createNewFile();
            InstagramApp.log("File created : " + save_file.getAbsolutePath());
            FileOutputStream ostream = new FileOutputStream(save_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.close();
            addFileToMediaDatabase(save_path);
            InstagramApp.toast(context, "Saved at: " + save_path);

        }
        catch (Exception e) {
            InstagramApp.toast(context, "Imeshindwa kusave");
            e.printStackTrace();
        }
    }

    private String getTimeStamp() {
        return new Timestamp(new Date().getTime())
                .toString()
                .replaceAll("\\.","_")
                .replaceAll(" ","_");
    }

    private void addFileToMediaDatabase(String file_path) {
        MediaScannerConnection.scanFile(this,
                new String[]{file_path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        InstagramApp.log("File is now on gallery");
                    }
                });
    }

    private void saveVideo(String video_url, final String save_path) {
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

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            InstagramApp.toast(getApplicationContext(), "Video saved at: " + save_path);
                            InstagramApp.log("Video saved at: " + save_path);
                            addFileToMediaDatabase(save_path);
                        }
                    });

                } finally {
                    if (in != null) in.close();
                    if (fout != null) fout.close();
                }

            }

        });
    }


    private void publishProgress(final double v) {
        //InstagramApp.log("Progress : "+v);
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                tvToolbar.setText(""+v);
                //InstagramApp.toast(getApplicationContext(), "Video saved at: " + save_path);
                //InstagramApp.log("Video saved at: " + save_path);
                //addFileToMediaDatabase(save_path);
            }
        });
    }


    private void createInstagramIntent(String type, String mediaPath){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        //set package
        share.setPackage("com.instagram.android");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_TEXT, "@InstaShusha");

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

        //set package
        share.setPackage("com.instagram.android");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_TEXT,"@InstaShusha");

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



