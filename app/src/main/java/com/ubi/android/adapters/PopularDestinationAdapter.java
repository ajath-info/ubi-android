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
import com.ubi.android.models.PopularDestinationModel;

import java.util.ArrayList;

public class PopularDestinationAdapter extends RecyclerView.Adapter<PopularDestinationAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<PopularDestinationModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    public boolean viewall = false;

    public PopularDestinationAdapter(Activity context, ArrayList<PopularDestinationModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_category_populardestination_layout, parent, false);
        if (viewall)
            view = layoutInflater.inflate(R.layout.adapter_cat_populardestination_viewalllayout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            PopularDestinationModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.icon)) {
                Picasso.get().load(categories.icon).into(holder.image);
            }
            holder.dealtext.setText(categories.deals);
            holder.nametxt.setText(categories.name);
            holder.toplay.setOnClickListener(new View.OnClickListener() {
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
        TextView nametxt, dealtext;
        LinearLayout toplay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nametxt = itemView.findViewById(R.id.nametxt);
            dealtext = itemView.findViewById(R.id.dealtext);
            toplay = itemView.findViewById(R.id.toplay);
        }

    }
}
