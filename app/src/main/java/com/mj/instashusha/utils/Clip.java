package com.mj.instashusha.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by Frank on 3/8/2016.
 * for clipboard interactions
 */
public class Clip {

    public static String hasInstagramUrl(Context context) {
        //only test if the text in clipboard contains an instagram link
        //should return link or empty string
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String copied_text = null;
        if (cm != null && cm.hasPrimaryClip()) {
            ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
            copied_text = item.getText().toString();
        }

        boolean condition = copied_text != null && copied_text.length() > 25 && copied_text.contains("https://www.instagram.com");
        return condition ? copied_text : "";
    }

}
