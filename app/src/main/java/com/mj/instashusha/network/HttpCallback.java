package com.mj.instashusha.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

public abstract class HttpCallback implements Callback, Instagrammable {

    private Instagrammable instagrammable = this;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            processResponse(response);
        } else {
            //do something if response code != 200
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
