package com.mj.instashusha.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Frank on 12/22/2015.
 *
 */
public class DopeTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String FONT_PATH = "dope_font.ttf"; //actually it's dosis

    public DopeTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public DopeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public DopeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(FONT_PATH, context);
        setTypeface(customFont);
    }

}
