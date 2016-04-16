package com.mj.instashusha.network;

/**
 * Created by Frank on 3/17/2016.
 *
 */
public interface ProgressListener {

    void onProgress(int progress);

    void onStart();

    void onCompleted(String save_path);
}
