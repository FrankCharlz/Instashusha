package com.mj.instashusha.network;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Frank on 12/19/2015.
 */
public interface Instagrammable {

    //used to post rb to the main thread
    void onUrlResponse(InstaResponse ir);

    void onVideoResponse(InputStream stream, long size) throws IOException;
}
