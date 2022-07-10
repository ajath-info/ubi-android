package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.ubi.android.models.AutomobileModel;

import java.util.ArrayList;

public class AutoMobileAdapter extends RecyclerView.Adapter<AutoMobileAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<AutomobileModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    public boolean isviewall = false;
    int width = 0;

    public AutoMobileAdapter(Activity context, ArrayList<AutomobileModel> categoriesModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.categoriesModels = categoriesModels;
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }


    public void setListner(OnAdapterItemClickListner listner) {
        this.listner = listner;
    }

    @NonNull
    @Override
    public dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_automobile_layout, parent, false);
        if (isviewall)
            view = layoutInflater.inflate(R.layout.adapter_automobile_layout_viewall, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            AutomobileModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getIcon())) {
                Picasso.get().load(categories.getIcon()).into(holder.image);
            }
            holder.text.setText(categories.getName());
            holder.toplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });
//            if (isviewall) {
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (width / 2));
//                holder.toplay.setLayoutParams(params);
//            }
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
        TextView text, subtext;
        LinearLayout toplay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            subtext = itemView.findViewById(R.id.subtext);
            toplay = itemView.findViewById(R.id.toplay);
        }

    }
}
