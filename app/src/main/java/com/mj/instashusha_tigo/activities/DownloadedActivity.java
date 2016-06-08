package com.mj.instashusha_tigo.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha_tigo.InstagramApp;
import com.mj.instashusha_tigo.R;
import com.mj.instashusha_tigo.adapters.FileListAdapter;
import com.mj.instashusha_tigo.utils.DopeTextView;
import com.mj.instashusha_tigo.utils.MenuClick;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class DownloadedActivity extends AppCompatActivity {


    private static final int MAX_DISPLAY_FILES = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);

        initViews();

        //loadAds();

        File items[] = InstagramApp.getAppFolder().listFiles();

        Arrays.sort(items, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                return Long.valueOf(b.lastModified()).compareTo(a.lastModified());
            }
        });


        if (items.length > MAX_DISPLAY_FILES)
            items = Arrays.copyOfRange(items, 0, MAX_DISPLAY_FILES);

        /***
         *
         try {
         FileInputStream dbInStream = openFileInput(Constants.DB_NAME);
         BufferedReader rd = new BufferedReader(new InputStreamReader(dbInStream));
         String line;
         while ((line = rd.readLine()) != null) {
         items.add(line.trim());
         InstagramApp.log("read : " + line);
         }

         rd.close();
         dbInStream.close();
         } catch (IOException e) {
         e.printStackTrace();
         }

         Collections.reverse(items); //sort by date...

         List<String> items_short;
         if (items.size() > MAX_DISPLAY_FILES) items_short = items.subList(0, MAX_DISPLAY_FILES);
         else items_short = items;

         */

        FileListAdapter adapter = new FileListAdapter(this, items);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_downloaded);
        mRecyclerView.setHasFixedSize(true);

        final StaggeredGridLayoutManager sl = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sl);
        mRecyclerView.setAdapter(adapter);

        //add on scroll listener...

/*
        Tracker mTracker = ((InstagramApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_DOWNLOADED_ACTIVITY");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
*/

    }

    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initViews() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_universal);
        if (toolbar != null)
            toolbar.setBackgroundColor(Color.BLACK);
        else {
            InstagramApp.log("Toolbar is null");
        }


        final ImageView btnMenu = (ImageView) findViewById(R.id.toolbar_action_settings_home);
        btnMenu.setOnClickListener(new MenuClick(this));

        final DopeTextView titleTv = (DopeTextView) findViewById(R.id.tv_appname);
        titleTv.setText("Downloaded");

    }


}
