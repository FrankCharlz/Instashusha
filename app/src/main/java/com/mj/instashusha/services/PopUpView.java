package com.mj.instashusha.services;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mj.instashusha.R;

/**
 * Created by Frank on 3/12/2016.
 * -kemmy's tycs graduation @ chief sec school...
 */
public class PopUpView {

    private final Context context;
    private WindowManager windowManager;
    private View popUpView;
    private WindowManager.LayoutParams params;

    public PopUpView(Context context) {
        this.context = context;
        init();
    }

    private  void init() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        popUpView = inflater.inflate(R.layout.service_pop_up, null);


        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 64;
        params.y = 64;


        ImageView clear = (ImageView) popUpView.findViewById(R.id.imgv_popup_clear);
        clear.setOnClickListener(clearClicked);

    }

    public void show() {
        windowManager.addView(popUpView, params);
    }

    private View.OnClickListener clearClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            windowManager.removeView(popUpView);
        }
    };
}
