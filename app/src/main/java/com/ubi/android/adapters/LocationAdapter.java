package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.ubi.android.models.Locations;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<Locations> categoriesModels;
    private OnAdapterItemClickListner listner;


    public LocationAdapter(Activity context, ArrayList<Locations> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.locations_item_lay, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            Locations categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.icon)) {
                Picasso.get().load(categories.icon).into(holder.flagimg);
            }
            holder.countrytxt.setText(categories.country_name);
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
        ImageView flagimg;
        TextView countrytxt;
        LinearLayout mainlay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mainlay = itemView.findViewById(R.id.mainlay);
            countrytxt = itemView.findViewById(R.id.countrytxt);
            flagimg = itemView.findViewById(R.id.flagimg);

        }

    }
}
