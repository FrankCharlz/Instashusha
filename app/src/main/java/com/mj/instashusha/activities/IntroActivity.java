package com.mj.instashusha.activities;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.mj.instashusha.R;

/**
 * Created by Frank on 12/22/2015.
 * God is good
 */
public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {


        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.maelezo_title_1),
                getResources().getString(R.string.maelezo_body_1),
                R.drawable.ins2,
                Color.parseColor("#2196F3")
        ));

        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.maelezo_title_2),
                getResources().getString(R.string.maelezo_body_2),
                R.drawable.ins1,
                Color.parseColor("#145214")
        ));

        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.maelezo_title_3),
                getResources().getString(R.string.maelezo_body_3),
                R.drawable.ins_logo,
                Color.parseColor("#020302")
        ));

        setFadeAnimation();
        //setZoomAnimation();
        //setDepthAnimation();

    }


    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        finish();

    }

    @Override
    public void onSlideChanged() {

    }
}
