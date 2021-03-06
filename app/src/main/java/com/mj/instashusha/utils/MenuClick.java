package com.mj.instashusha.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.mj.instashusha.MyApp;
import com.mj.instashusha.activities.MainActivity;

/**
 * Created by Frank on 12/22/2015.
 */
public class MenuClick implements View.OnClickListener {
    private final Context context;

    public MenuClick(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        MyApp.log("Home clicked...");
        //start main activity and go to instruction fragment...
        Intent t = new Intent(context, MainActivity.class);
        t.putExtra(MyApp.GO_TO_INSTRUCTIONS, true);
        context.startActivity(t);

    }
}
