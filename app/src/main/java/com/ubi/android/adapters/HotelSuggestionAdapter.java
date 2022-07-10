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
import com.ubi.android.models.SuggestionModel;

import java.util.ArrayList;

public class HotelSuggestionAdapter extends RecyclerView.Adapter<HotelSuggestionAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<SuggestionModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    public boolean viewall = false;

    public HotelSuggestionAdapter(Activity context, ArrayList<SuggestionModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_category_hotelsuggestion_layout, parent, false);
        if (viewall)
            view = layoutInflater.inflate(R.layout.adapter_cat_suggestion_viewall, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            SuggestionModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getIcon())) {
                Picasso.get().load(categories.getIcon()).into(holder.image);
            }
            holder.dealtext.setText(categories.getAvg_rating());
            holder.nametxt.setText(categories.getName());
            holder.totaltext.setText("(" + categories.getTotla_rating() + ")");
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

            holder.hearticon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

            if (!TextUtils.isEmpty(categories.getIs_fav()) && categories.getIs_fav().equalsIgnoreCase("true"))
                holder.hearticon.setImageResource(R.drawable.ic_heart_selected);
            else
                holder.hearticon.setImageResource(R.drawable.ic_heart_unselected);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    static class dataViewHolder extends RecyclerView.ViewHolder {
        ImageView image, hearticon;
        TextView nametxt, dealtext, totaltext;
        LinearLayout mainlay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            hearticon = itemView.findViewById(R.id.hearticon);
            mainlay = itemView.findViewById(R.id.mainlay);
            image = itemView.findViewById(R.id.image);
            nametxt = itemView.findViewById(R.id.nametxt);
            dealtext = itemView.findViewById(R.id.dealtext);
            totaltext = itemView.findViewById(R.id.totaltext);
        }

    }
}
