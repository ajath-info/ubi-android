package com.ubi.android.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.ubi.android.R;

public class PoppinsMediumEditTextView extends AppCompatEditText {

    public PoppinsMediumEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public PoppinsMediumEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    public PoppinsMediumEditTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), "Poppins-Medium.ttf");
        setTypeface(customFontTypeface);
    }
}
