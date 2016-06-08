package com.mj.instashusha_tigo.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mj.instashusha_tigo.InstagramApp;
import com.mj.instashusha_tigo.R;
import com.mj.instashusha_tigo.fragments.InstructionFragment;
import com.mj.instashusha_tigo.services.PopUpService;
import com.mj.instashusha_tigo.utils.Clip;
import com.mj.instashusha_tigo.utils.OneTimeOps;
import com.mj.instashusha_tigo.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private boolean waking_from_pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        //hide home icon.. Because it not used here in this activity..
        //findViewById(R.id.toolbar_action_settings_home).setVisibility(View.INVISIBLE);


        if (getIntent().getBooleanExtra(InstagramApp.GO_TO_INSTRUCTIONS, false)) {
            showInstructionFragment();
        } else {
            programFlow();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                OneTimeOps.checkAppIntroduction(context);
                OneTimeOps.getUserEmail(context);
            }
        }).start();

        checkAndStartBackgroundService();

    }


    private void programFlow() {
        //if back from save activity, then just show instruction fragment then chill...
        InstagramApp.log("from save activity : " + InstagramApp.BACK_FROM_SAVE_ACTIVITY);
        if (InstagramApp.BACK_FROM_SAVE_ACTIVITY) {
            InstagramApp.BACK_FROM_SAVE_ACTIVITY = false;
            showInstructionFragment();
            return;
        }

        String url = Clip.getInstagramUrl(this);
        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            InstagramApp.log("No instagram url in clipboard");
            showInstructionFragment();
            return;

        }

        //all conditions GOOD proceed to process instagram link
        InstagramApp.log("URL is : " + url);
        if (Utils.isTheLastUr(context, url)) {
            //no need to saveStream it again...
            showInstructionFragment();
        } else {
            proceedLoading(url);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //do not perform fragment transaction here..
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //method is guaranteed to be called after the Activity has been restored to its original state
        if (waking_from_pause) {
            //onresume is called after oncreate at first..
            programFlow();
            waking_from_pause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        waking_from_pause = true;
    }

    private void showInstructionFragment() {
        InstructionFragment fragment = new InstructionFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public void proceedLoading(final String url) {
        Intent intent = new Intent(context, SaveActivity.class);
        intent.putExtra(SaveActivity.SRC_URL, url);
        startActivity(intent);
        //finish();

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkAndStartBackgroundService() {
        //check if service was started successfully, if not start it now...
        Intent serviceIntent = new Intent(getBaseContext(), PopUpService.class);
        boolean sup = isServiceRunning(PopUpService.class);
        if (!sup) {
            InstagramApp.log("service was not running, I gotta start it..");
            startService(serviceIntent);
        } else {
            InstagramApp.log("service was still running");
        }
    }


}
