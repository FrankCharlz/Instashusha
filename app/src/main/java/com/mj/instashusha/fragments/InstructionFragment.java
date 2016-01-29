package com.mj.instashusha.fragments;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.activities.DownloadedActivity;
import com.mj.instashusha.activities.IntroActivity;
import com.mj.instashusha.utils.DopeTextView;
import com.squareup.okhttp.internal.Util;

import java.util.logging.Handler;

/**
 * Created by Frank on 12/19/2015.
 */
public class InstructionFragment extends Fragment {

    private Context context;
    private DopeTextView instructionTv;
    private int counter;
    private int total;
    private String[] messages;
    private Animation swap, swapIn;
    private Animation swap0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_instruction, container, false);
        context = getActivity();

        //money baby...
        final AdView mAdView = (AdView) root.findViewById(R.id.adView_fragment);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_HASH").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        final DopeTextView btnViewDownloaded = (DopeTextView) root.findViewById(R.id.btn_view_downloaded);
        btnViewDownloaded.setOnClickListener(new ButtonClicks());

        final DopeTextView btnOpenTutorial = (DopeTextView) root.findViewById(R.id.btn_maelezo_zaidi);
        btnOpenTutorial.setOnClickListener(new ButtonClicks());

        final DopeTextView btnOpenInsta = (DopeTextView) root.findViewById(R.id.btn_fungua_insta);
        btnOpenInsta.setOnClickListener(new ButtonClicks());

        messages = context.getResources().getStringArray(R.array.ins_messages);
        counter = 0;
        total = messages.length;


        instructionTv = (DopeTextView) root.findViewById(R.id.tv_instructions);
        instructionTv.setOnTouchListener(null);

        swap = AnimationUtils.loadAnimation(context, R.anim.in_out);

        return root;

    }

    public static class FuckingFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fucking_fragment, container, false);
        }
    }

    /***
     *  class Touch implements View.OnTouchListener {

    @Override
    public boolean onTouch(View view, MotionEvent event) {
    float x0 = 0, x1 = 0;
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
    x0 = event.getX();
    break;
    case MotionEvent.ACTION_UP:
    x1 = event.getX();
    break;
    default:  break;
    }

    if (x1 - x0 > 60) swapRight();
    else if (x1 - x0 > 60) swapLeft();

    return true;
    }
    }

     /*
     private void swapLeft() {
     InstagramApp.log("Swapping left");
     TranslateAnimation tr = new TranslateAnimation(
     0,
     instructionTv.getWidth(),
     0,
     0);
     tr.setDuration(1000);
     tr.setAnimationListener(new MyAnimationListener());
     instructionTv.startAnimation(swap);

     }

     private void swapRight() {
     InstagramApp.log("Swapping right");
     TranslateAnimation tr = new TranslateAnimation(
     0,
     -1*instructionTv.getWidth(),
     0,
     0);
     tr.setDuration(1000);
     tr.setAnimationListener(new MyAnimationListener());
     instructionTv.startAnimation(swap);

     }


     class MyAnimationListener implements Animation.AnimationListener {

    @Override
    public void onAnimationStart(Animation animation) {
    InstagramApp.log("Animation starting : counter : "+counter);

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    InstagramApp.log("Animation ending : counter : " + counter);
    InstagramApp.log("Anim : "+animation.toString());
    counter++;
    counter = counter % total;
    instructionTv.setText(messages[counter]);
    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    }

     */


    class ButtonClicks implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_view_downloaded:
                    Intent t = new Intent(context, DownloadedActivity.class);
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
