package com.mj.instashusha.network;

/**
 * Created by Frank on 12/18/2015.
 */
public class InstaResponse {
    public int success;
    public String type;
    public String image_url;
    public String video_url;

    public String toString() {
        return "\n"
                +success+"\n-"
                +type+"\n-"
                +image_url+"\n-"
                +video_url;

    }
}
