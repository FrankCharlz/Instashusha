package com.mj.instashusha.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.adapters.FileListAdapter;
import com.mj.instashusha.utils.Utils;

import java.io.File;

public class DownloadedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        if (toolbar != null) toolbar.setBackgroundColor(Utils.addAlphaToColor(Color.BLACK, 0.32f));

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        File pics[] = new File(InstagramApp.PHOTO_FOLDER_PATH).listFiles();
        File vids[] = new File(InstagramApp.VIDEO_FOLDER_PATH).listFiles();

        FileListAdapter adapter = new FileListAdapter(this, pics, vids);
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_downloaded);
        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager sl = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(sl);

        mRecyclerView.setAdapter(adapter);

    }


}
