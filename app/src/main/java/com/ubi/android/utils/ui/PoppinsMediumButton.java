package com.ubi.android.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PoppinsMediumButton extends androidx.appcompat.widget.AppCompatButton {

    public PoppinsMediumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public PoppinsMediumButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    public PoppinsMediumButton(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Medium.ttf");
        setTypeface(customFontTypeface);
    }
}
