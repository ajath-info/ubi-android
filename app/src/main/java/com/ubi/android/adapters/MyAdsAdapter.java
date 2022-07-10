package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.MyAds;

import java.util.ArrayList;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<MyAds> categoriesModels;
    private OnAdapterItemClickListner listner;


    public MyAdsAdapter(Activity context, ArrayList<MyAds> categoriesModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.categoriesModels = categoriesModels;
    }


    public void setListner(OnAdapterItemClickListner listner) {
        this.listner = listner;
    }

    @NonNull
    @Override
    public dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_myads_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            MyAds categories = categoriesModels.get(position);
            if (categories.getImage().size() > 0 && !TextUtils.isEmpty(categories.getImage().get(0))) {
                Picasso.get().load(categories.getImage().get(0)).into(holder.image);
            }
            holder.titletv.setText(Html.fromHtml(categories.getName()));
            holder.pricetv.setText("$" + categories.getPrice());
            holder.locationtv.setText(Html.fromHtml(categories.getLocation()));
            holder.desctv.setText(Html.fromHtml(categories.getDescription()));
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    static class dataViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView titletv, pricetv, locationtv, desctv;
        LinearLayout mainlay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            desctv = itemView.findViewById(R.id.desctv);
            image = itemView.findViewById(R.id.image);
            pricetv = itemView.findViewById(R.id.pricetv);
            titletv = itemView.findViewById(R.id.titletv);
            locationtv = itemView.findViewById(R.id.locationtv);
            mainlay = itemView.findViewById(R.id.mainlay);
        }

    }
}
