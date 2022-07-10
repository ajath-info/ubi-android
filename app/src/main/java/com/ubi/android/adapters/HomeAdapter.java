package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.HomeData;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    HomeData homeData;
    private OnAdapterItemClickListner listner;
    int count;

    public HomeAdapter(Activity context, HomeData homeData) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.homeData = homeData;
    }


    public void setListner(OnAdapterItemClickListner listner) {
        this.listner = listner;
    }

    @NonNull
    @Override
    public dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_home_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return count;
    }

    static class dataViewHolder extends RecyclerView.ViewHolder {
        ViewPager topbannerpager;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            topbannerpager = itemView.findViewById(R.id.topbannerpager);
        }

    }
}
