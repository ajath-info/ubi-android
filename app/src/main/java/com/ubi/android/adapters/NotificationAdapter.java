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
import com.ubi.android.models.NotificationModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<NotificationModel> categoriesModels;
    private OnAdapterItemClickListner listner;


    public NotificationAdapter(Activity context, ArrayList<NotificationModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.notification_item_lay, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            NotificationModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getProfile_img())) {
                Picasso.get().load(categories.getProfile_img()).into(holder.img);
            }
            holder.countrytxt.setText(categories.getMessage());
            holder.timetv.setText(categories.getCreated_at());
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
        TextView countrytxt, timetv;
        LinearLayout mainlay;
        ImageView img;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            timetv = itemView.findViewById(R.id.timetv);
            mainlay = itemView.findViewById(R.id.mainlay);
            countrytxt = itemView.findViewById(R.id.countrytxt);
        }

    }
}
