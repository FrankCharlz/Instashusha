package com.mj.instashusha.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.Constants;
import com.mj.instashusha.MyApp;
import com.mj.instashusha.R;
import com.mj.instashusha.adapters.FileListAdapter;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.MenuClick;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class DownloadedActivity extends AppCompatActivity {


    private static final int MAX_DISPLAY_FILES = 32; //my age by june 20, 2016 + 9
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);

        //load an interstitial with probability of 3/7
        if (new Random().nextInt(100) < 5) {
            MyApp.log("Loading interstitial...");
            loadAdInterstitial();
        }

        if (new Random().nextInt(100) < 60) {
            MyApp.log("Loading banner...");
            loadAds(); //load banner..
        }


        initViews();
        track();


        File items[] = MyApp.getAppFolder().listFiles();

        if (items == null) return;

        Arrays.sort(items, new Comparator<File>() {
            @Override
            public int compare(File a, File b) {
                return Long.valueOf(b.lastModified()).compareTo(a.lastModified());
            }
        });


        if (items.length > MAX_DISPLAY_FILES)
            items = Arrays.copyOfRange(items, 0, MAX_DISPLAY_FILES);


        FileListAdapter adapter = new FileListAdapter(this, items);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_downloaded);

        final StaggeredGridLayoutManager sl = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(sl);
            mRecyclerView.setAdapter(adapter);
        }

        //todo: add on scroll listener...

    }

    private void loadAdInterstitial() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.DA_I_AD_ID);

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                MyApp.log("Interstitial is loaded");
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                MyApp.log("Interstitial failed to load : "+errorCode);
            }
        });
    }

    private void track() {
        Tracker mTracker = ((MyApp) getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_DOWNLOADED_ACTIVITY");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView_activity_finisher);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initViews() {

        final DopeTextView titleTv = (DopeTextView) findViewById(R.id.tv_appname);
        titleTv.setText(R.string.downloaded_activity_title);

    }


}
