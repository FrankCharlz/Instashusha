package com.mj.instashusha.activities;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mj.instashusha.InstagramApp;
import com.mj.instashusha.R;
import com.mj.instashusha.fragments.InstructionFragment;
import com.mj.instashusha.services.Adele;
import com.mj.instashusha.services.BootReceiver;
import com.mj.instashusha.services.PopUpService;
import com.mj.instashusha.utils.Utils;

public class MainActivity extends AppCompatActivity {

    public static final String SRC_URL = "0x00f12";

    private Context context;
    private boolean waking_from_pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide home icon.. Because it not used here in this activity..
        findViewById(R.id.toolbar_action_settings_home).setVisibility(View.GONE);

        context = this;
        checkAppIntroduction();

        if (getIntent().getBooleanExtra(InstagramApp.GO_TO_INSTRUCTIONS, false)) {
            showInstructionFragment();
            return;
        }

        programFlow();

        /*/check if service was started successfully, if not start it now...
        boolean sup = isServiceRunning(PopUpService.class);
        if (!sup) {
            InstagramApp.log("service was not running, I gotta start it..");
            startService(new Intent(this, PopUpService.class));
        } else {
            InstagramApp.log("service was still running");
        }
        */

        Intent serviceIntent = new Intent(context, Adele.class);

        boolean alarmUp = (PendingIntent.getBroadcast(
                context,
                0,
                serviceIntent,
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            InstagramApp.log("Alarm is aleard started");
        } else {
            InstagramApp.log("Alarm not started");
            //start alarm
            Intent i = new Intent(BootReceiver.CUSTOM_BROADCAST_ACTION_STRING);
            sendBroadcast(i);
        }
    }



    private void programFlow() {
        //if back from save activity, then just show instruction fragment then chill...
        InstagramApp.log("from save activity : " + InstagramApp.BACK_FROM_SAVE_ACTIVITY);
        if (InstagramApp.BACK_FROM_SAVE_ACTIVITY) {
            InstagramApp.BACK_FROM_SAVE_ACTIVITY = false;
            showInstructionFragment();
            return;
        }

        String url = InstagramApp.getLinkFromClipBoard(this);
        if (url.isEmpty()) {
            //no instagram link in clipboard
            //launch instruction fragment
            InstagramApp.log("No instagram url in clipboard");
            showInstructionFragment();
            return;

        }

        //all conditions GOOD proceed to process instagram link
        InstagramApp.log("URL is : " + url);
        if(Utils.isTheLastUr(context, url)) {
            //no need to download it again...
            showInstructionFragment();
        } else {
            proceedLoading(url);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void checkAppIntroduction() {
        //using thread to read prefs...
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {

                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    getPrefs.edit().putBoolean("firstStart", false).apply();
                }
            }
        });
        t.start();

    }

    public void proceedLoading(final String url) {
        Intent intent = new Intent(context, SaveActivity.class);
        intent.putExtra(SRC_URL, url);
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

}
