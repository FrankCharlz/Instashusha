package com.mj.instashusha.network;

import java.io.Serializable;

/**
 * Created by Frank on 12/18/2015.
 */
public class InstaResponse implements Serializable {
    public static final String SERIALIZE = "jb7j";
    public int success;
    public String type, source_url;
    public String image_url;
    public String video_url; //empty if media is image..

    public String toString() {
        return "\n"
                + success + "\n-"
                + type + "\n-"
                + image_url + "\n-"
                + video_url;

    }
}
