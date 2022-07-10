package com.ubi.android.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class PoppinsBoldTextView extends androidx.appcompat.widget.AppCompatTextView {

    public PoppinsBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public PoppinsBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    public PoppinsBoldTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Bold.ttf");
        setTypeface(customFontTypeface);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
}
