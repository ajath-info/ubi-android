package com.ubi.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;

import java.util.ArrayList;

public class ProductDetailImageAdapter extends PagerAdapter {
    Activity context;
    LayoutInflater mLayoutInflater;
    ArrayList<String> images = new ArrayList<>();

    public ProductDetailImageAdapter(Activity context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.product_detail_image_adapter, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageview);
        if (!TextUtils.isEmpty(images.get(position))) {
            Picasso.get().load(images.get(position)).into(imageView);
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
