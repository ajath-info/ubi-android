package com.ubi.android.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ubi.android.R;

import java.util.List;

public class GenderSpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    Typeface font = Typeface.createFromAsset(getContext().getAssets(),
            "Poppins-Medium.ttf");
    int fontsize=13;

    public GenderSpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
        view.setTypeface(font);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontsize);
        view.setTypeface(font);
        return view;
    }
}