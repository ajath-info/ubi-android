package com.ubi.android.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class PoppinsRegularTextView extends androidx.appcompat.widget.AppCompatTextView {

    public PoppinsRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public PoppinsRegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    public PoppinsRegularTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Regular.ttf");
        setTypeface(customFontTypeface);
    }
}
