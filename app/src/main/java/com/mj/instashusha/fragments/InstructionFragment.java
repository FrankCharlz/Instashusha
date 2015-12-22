package com.mj.instashusha.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.FinisherActivity;
import com.mj.instashusha.activities.IntroActivity;

/**
 * Created by Frank on 12/19/2015.
 */
public class InstructionFragment extends Fragment {

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_instruction, container, false);
        context = getActivity();

        final Button btnViewDownloaded = (Button) root.findViewById(R.id.btn_view_downloaded);
        btnViewDownloaded.setOnClickListener(new ButtonClicks());

        final Button btnOpenTutorial = (Button) root.findViewById(R.id.btn_maelezo_zaidi);
        btnOpenTutorial.setOnClickListener(new ButtonClicks());

        final Button btnOpenInsta = (Button) root.findViewById(R.id.btn_fungua_insta);
        btnOpenInsta.setOnClickListener(new ButtonClicks());


        //money baby...
        AdView mAdView = (AdView) root.findViewById(R.id.adView_fragment);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();
        mAdView.loadAd(adRequest);

        return root;
    }

    class ButtonClicks implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_view_downloaded:
                    Intent t = new Intent(context, FinisherActivity.class);
                    startActivity(t);
                    break;
                case R.id.btn_maelezo_zaidi:
                    Intent t2 = new Intent(context, IntroActivity.class);
                    startActivity(t2);
                    break;

                case R.id.btn_fungua_insta:
                    try {
                        Intent instaIntent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                        startActivity(instaIntent);
                        //getActivity().finish();
                    } catch (Exception e) {
                        InstagramApp.log(e.getLocalizedMessage());
                    }
                    break;

                default: break;

            }
        }
    }
}
