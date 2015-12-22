package com.mj.instashusha.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.adapters.FilesListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FinisherActivity extends AppCompatActivity {

    final private  ArrayList<File> filesArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();
        mAdView.loadAd(adRequest);

        File pics[] = new File(InstagramApp.getAppPhotoFolder()).listFiles();
        File vids[] = new File(InstagramApp.getAppVideoFolder()).listFiles();

        Collections.addAll(filesArray, pics);
        Collections.addAll(filesArray, vids);

        //sort by lastest...
        Collections.sort(filesArray, new Comparator<File>() {
            @Override
            public int compare(File f2, File f1) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        FilesListAdapter adapter = new FilesListAdapter(this, filesArray);
        ListView listViewFiles = (ListView) findViewById(R.id.list_view_files);
        listViewFiles.setAdapter(adapter);
    }

}
