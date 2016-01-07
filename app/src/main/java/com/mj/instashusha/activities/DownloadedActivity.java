package com.mj.instashusha.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.adapters.FilesListAdapter;

import java.io.File;

public class DownloadedActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finisher);

        //final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings);
        btnMenu.setOnClickListener(new MenuClick(this));


        final ListView listViewFiles = (ListView) findViewById(R.id.list_view_files);

        File pics[] = new File(InstagramApp.PHOTO_FOLDER_PATH).listFiles();
        File vids[] = new File(InstagramApp.VIDEO_FOLDER_PATH).listFiles();

        FilesListAdapter adapter = new FilesListAdapter(this, pics, vids);
        listViewFiles.setAdapter(adapter);
    }


}
