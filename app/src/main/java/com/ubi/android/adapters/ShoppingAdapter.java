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
import com.ubi.android.models.ShoppingModel;

import java.util.ArrayList;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<ShoppingModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    public boolean isviewall = false;
    int cardbgs[] = {R.color.shoppingbg1, R.color.shoppingbg2,
            R.color.shoppingbg3,
            R.color.shoppingbg4};
    int count = 0;

    public ShoppingAdapter(Activity context, ArrayList<ShoppingModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_shopping_layout, parent, false);
        if (isviewall)
            view = layoutInflater.inflate(R.layout.adapter_shopping_layout_viewall, parent, false);

        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ShoppingModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getIcon())) {
                Picasso.get().load(categories.getIcon()).into(holder.image);
            }
            holder.text.setText(categories.getTitle());
            holder.subtext.setText(categories.getName());
            holder.mainlay.setBackgroundResource(cardbgs[count]);
            count++;
            if (count >= cardbgs.length)
                count = 0;

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
        TextView text, subtext;
        LinearLayout mainlay;
        LinearLayout toplay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            subtext = itemView.findViewById(R.id.subtext);
            mainlay = itemView.findViewById(R.id.mainlay);
            toplay = itemView.findViewById(R.id.toplay);
        }

    }
}
