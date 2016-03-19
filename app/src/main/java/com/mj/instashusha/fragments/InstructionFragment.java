package com.mj.instashusha.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mj.instashusha.Constants;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.DownloadedActivity;
import com.mj.instashusha.activities.IntroActivity;
import com.mj.instashusha.utils.DopeTextView;
import com.mj.instashusha.utils.Utils;

/**
 * Created by Frank on 12/19/2015.
 * God bless
 */
public class InstructionFragment extends Fragment {

    private Context context;
    private View rootContainer;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker mTracker = ((InstagramApp) getActivity().getApplication()).getDefaultTracker();
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.setScreenName("SCREEN_INSTRUCTION_FRAGMENT");

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_instruction, container, false);
        context = getActivity();

        //loadAds();

        final DopeTextView btnViewDownloaded = (DopeTextView) root.findViewById(R.id.btn_view_downloaded);
        String s = context.getResources().getString(R.string.view_downloaded)+" ("
                +InstagramApp.mediaDownloaded()+")";
        btnViewDownloaded.setText("");
        btnViewDownloaded.setText(s);
        btnViewDownloaded.setOnClickListener(new ButtonClicks());

        final DopeTextView btnOpenTutorial = (DopeTextView) root.findViewById(R.id.btn_maelezo_zaidi);
        btnOpenTutorial.setOnClickListener(new ButtonClicks());
        Utils.setBackgroundColor(btnOpenTutorial, Utils.getColor(context, R.color.blue_t));

        final DopeTextView btnOpenInsta = (DopeTextView) root.findViewById(R.id.btn_fungua_insta);
        btnOpenInsta.setOnClickListener(new ButtonClicks());

        //make all buttons equal width...
        int goodWidth = btnViewDownloaded.getWidth();
        //btnOpenInsta.setWidth(goodWidth);
        //btnOpenTutorial.setWidth(goodWidth);

        rootContainer = container;

       View ta = root.findViewById(R.id.tigo_ad_instruction_activity);
        ta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open browser...
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.TIGO_URL));
                context.startActivity(intent);
            }
        });

        return root;

    }

    private void loadAds() {
        final AdView mAdView = (AdView) root.findViewById(R.id.adView_fragment);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    class ButtonClicks implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_maelezo_zaidi:
                    Intent t2 = new Intent(context, IntroActivity.class);
                    startActivity(t2);
                    break;

                case R.id.btn_fungua_insta:
                    try {
                        Intent instaIntent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                        startActivity(instaIntent);
                    } catch (Exception e) {
                        InstagramApp.log(e.getLocalizedMessage());
                    }
                    break;

                case R.id.btn_view_downloaded:
                    int downloaded = InstagramApp.mediaDownloaded();
                    if (downloaded > 0) {
                        Intent t = new Intent(context, DownloadedActivity.class);
                        startActivity(t);
                    } else {
                        Snackbar
                                .make(rootContainer,
                                        "No media downloaded",
                                        Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    break;
                default: break;

            }
        }
    }
}
