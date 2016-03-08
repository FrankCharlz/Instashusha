/*
 * Copyright 2013 Tristan Waddington
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mj.instashusha.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors the {@link ClipboardManager} for changes and logs the text to a file.
 */
public class ClipService extends Service {
    private static final String FILENAME = "instashusha/clipboard-history.txt";

    private File mHistoryFile;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;
    private int NOTIFICATION_ID = 0;
    private NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Show an ongoing notification when this service is running.
        mHistoryFile = new File(getExternalFilesDir(null), FILENAME);

    }

    private void showNotif(String msg) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notif: "+ NOTIFICATION_ID)
                .setContentText(msg)
                .setContentIntent(contentIntent)
                .build();

        nm.notify(NOTIFICATION_ID++, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
        return  START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            InstagramApp.log("onPrimaryClipChanged");
            ClipData clip = mClipboardManager.getPrimaryClip();
            mThreadPool.execute(new WriteHistoryRunnable(
                    clip.getItemAt(0).getText()));

            //showNotif("xrunnable : "+clip.getItemAt(0).getText());

        }
    };

    private class WriteHistoryRunnable implements Runnable {
        private final Date mNow;
        private final CharSequence mTextToWrite;

        public WriteHistoryRunnable(CharSequence text) {
            mNow = new Date(System.currentTimeMillis());
            mTextToWrite = text;

            showNotif("in-runnable : "+text);
        }

        @Override
        public void run() {
            if (TextUtils.isEmpty(mTextToWrite)) {
                // Don't write empty text to the file
                return;
            }

            if (isExternalStorageWritable()) {
                try {
                    InstagramApp.log("Writing new clip to history:");
                    InstagramApp.log(mTextToWrite.toString());
                    BufferedWriter writer =
                            new BufferedWriter(new FileWriter(mHistoryFile, true));
                    writer.write(String.format("[%s]: ", mNow.toString()));
                    writer.write(mTextToWrite.toString());
                    writer.newLine();
                    writer.close();
                } catch (IOException e) {
                    InstagramApp.log(String.format("Failed to open file %s for writing!",
                            mHistoryFile.getAbsoluteFile()));
                }
            } else {
                InstagramApp.log("External storage is not writable!");
            }
        }
    }
}
