package com.mj.instashusha.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import android.support.v7.widget.Toolbar;
import com.mj.instashusha.adapters.FileListAdapter;
import com.mj.instashusha.utils.DownloadedItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DownloadedActivity extends AppCompatActivity {


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        if (toolbar != null)
            toolbar.setBackgroundColor(Color.BLACK);
        else {
            InstagramApp.log("Toolbar is null");
        }

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        File pics[] = new File(InstagramApp.PHOTO_FOLDER_PATH).listFiles();
        File vids[] = new File(InstagramApp.VIDEO_FOLDER_PATH).listFiles();


        ArrayList<File> items = new ArrayList<>(pics.length + vids.length);
        Collections.addAll(items, pics);
        Collections.addAll(items, vids);
        Collections.sort(items, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                return Long.valueOf(b.lastModified()).compareTo(a.lastModified());
            }
        });

        FileListAdapter adapter = new FileListAdapter(this, items);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_downloaded);
        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager sl = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sl);
        mRecyclerView.setAdapter(adapter);

    }


}
