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
import com.ubi.android.models.TopBanners;

import java.util.ArrayList;

public class TopBannerAdapter extends PagerAdapter {
    LayoutInflater mLayoutInflater;
    Activity context;
    ArrayList<TopBanners> data;

    public TopBannerAdapter(Activity context, ArrayList<TopBanners> data) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.topbanner_pager_item, container, false);
        TopBanners banners = data.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        if (!TextUtils.isEmpty(banners.getUrl()))
            Picasso.get().load(banners.getUrl()).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.9f;
    }
}
