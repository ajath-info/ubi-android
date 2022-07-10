package com.ubi.android.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MontserratSemiBoldTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MontserratSemiBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MontserratSemiBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    public MontserratSemiBoldTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "Montserrat-SemiBold.ttf");
        setTypeface(customFontTypeface);
    }
}
