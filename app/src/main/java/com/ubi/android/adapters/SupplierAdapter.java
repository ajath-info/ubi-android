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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ImportantSupplies;

import java.util.ArrayList;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<ImportantSupplies> categoriesModels;
    private OnAdapterItemClickListner listner;
    public boolean isviewall = false;

    public SupplierAdapter(Activity context, ArrayList<ImportantSupplies> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_supplier_layout, parent, false);
        if (isviewall)
            view = layoutInflater.inflate(R.layout.adapter_supplier_layout_viewall, parent, false);

        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ImportantSupplies categories = categoriesModels.get(position);
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
        TextView text;
        RelativeLayout toplay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            toplay = itemView.findViewById(R.id.toplay);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }

    }
}
