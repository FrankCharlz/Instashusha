package com.mj.instashusha;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mj.instashusha.fragments.InstructionFragment;
import com.mj.instashusha.network.HttpCallback;
import com.mj.instashusha.network.InstaResponse;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://insta-dl.appspot.com/dl?source=";
    public static final String MEDIA_TYPE = "media type";
    public static final String IMAGE_URL = "image url";
    public static final String VIDEO_URL = "video url";

    private Request request;
    private Context context;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        InstagramApp.makeAppFolder();

        String url = InstagramApp.getLinkFromClipBoard(this);

        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            InstagramApp.log("No instagram url in clipboard");

            InstructionFragment fragment = new InstructionFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.main_container, fragment);
            fragmentTransaction.commit();

        } else {
            //process instagram link
            InstagramApp.log("Link is : " + url);
            InstagramApp.toast(this, "URL : " + url);

            request = new Request.Builder()
                    .url(BASE_URL+ url)
                    .build();


            pro();
        }


    }

    public void pro() {

        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage("Subiri kidogo...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressBar.setContentView(R.layout.progress_bar_view);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        InstagramApp.getOkHttpClient().newCall(request).enqueue(
                new HttpCallback() {
                    @Override
                    public void onUrlResponse(InstaResponse ir) {
                        InstagramApp.log("If this fails try, MyInstagrammable");
                        InstagramApp.log(ir.toString());
                        Intent intent = new Intent(context, SaveActivity.class);
                        intent.putExtra(MEDIA_TYPE, ir.type);
                        intent.putExtra(IMAGE_URL, ir.image_url);
                        intent.putExtra(VIDEO_URL, ir.video_url);

                        //InstagramApp.getOkHttpClient().getDispatcher().getExecutorService().shutdown();
                        progressBar.dismiss();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onVideoResponse(InputStream stream, long size) throws IOException {

                    }

                });
    }

}
