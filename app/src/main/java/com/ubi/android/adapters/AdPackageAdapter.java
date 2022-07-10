package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.PackageModel;

import java.util.ArrayList;

public class AdPackageAdapter extends RecyclerView.Adapter<AdPackageAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<PackageModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    int color[] = {R.color.packagecolor1, R.color.packagecolor2, R.color.packagecolor3};


    public AdPackageAdapter(Activity context, ArrayList<PackageModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.ad_package_item, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            PackageModel categories = categoriesModels.get(position);
            holder.title.setText(categories.title);
            holder.description.setText(categories.description);
            holder.price.setText("$" + categories.price);
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });
            holder.rb.setChecked(categories.isChecked);
            if (position < color.length) {
                GradientDrawable drawable = (GradientDrawable) holder.lay.getBackground();
                drawable.setColor(context.getResources().getColor(color[position]));
                holder.lay.setBackground(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    static class dataViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        LinearLayout mainlay, lay;
        RadioButton rb;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            lay = itemView.findViewById(R.id.lay);
            description = itemView.findViewById(R.id.description);
            mainlay = itemView.findViewById(R.id.mainlay);
            price = itemView.findViewById(R.id.price);
            rb = itemView.findViewById(R.id.rb);
        }

    }
}
