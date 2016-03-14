package com.mj.instashusha.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mj.instashusha.InstagramApp;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;

public abstract class HttpCallback implements Callback, Instagrammable {

    private Instagrammable instagrammable = this;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onFailure(Call call, IOException e) {
        InstagramApp.log("okhttp request failed : "+e.getLocalizedMessage());
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            processResponse(response);
        } else {
            //do something if response code != 200
            InstagramApp.log("okhttp response code not 200");
        }
    }


    private void processResponse(Response response) throws IOException {
        final MediaType responseType = response.body().contentType();
        //responseType.type();
        //Returns the high-level media type, such as "text", "image", "audio", "video", or "application".

        if (responseType.type().equalsIgnoreCase("video")) {
            final InputStream inputStream = response.body().byteStream();
            final long size = response.body().contentLength();
            instagrammable.onVideoResponse(inputStream, size);

        }

        if (responseType.type().equalsIgnoreCase("application")) {
            //application/json
            Gson gson = new GsonBuilder().create();
            final InstaResponse instaResponse =
                    gson.fromJson(response.body().charStream(), InstaResponse.class);

            //stop here if success == 0
            if (instaResponse.success == 1) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        instagrammable.onUrlResponse(instaResponse);
                    }
                });
            }
        }


    }

}
