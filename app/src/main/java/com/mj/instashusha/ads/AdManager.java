package com.mj.instashusha.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mj.instashusha.Constants;
import com.mj.instashusha.InstagramApp;

/**
 * Created by Frank on 8/4/2016.
 * //to manage loading and displaying of interstitial ads...
 */
public class AdManager {


    private static InterstitialAd mInterstitialAd;

    public static void loadAd(Context context) {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Constants.DA_I_AD_ID);

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                InstagramApp.log("Interstitial has been loaded in manager...");
            }
        });

    }

    public static InterstitialAd getAd() {
        return mInterstitialAd;
    }

}
