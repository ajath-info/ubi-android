package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.CategoriesModel;

import java.util.ArrayList;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<CategoriesModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    GradientDrawable drawables[] = new GradientDrawable[5];
    boolean isviewall = false;
    //    int cardbgs[] = {R.color.cat1start, R.color.cat2start,
//            R.color.cat3start,
//            R.color.cat4start,
//            R.color.cat5start};
    int count = 0;

    public CategoryHomeAdapter(Activity context, ArrayList<CategoriesModel> categoriesModels, boolean isviewall) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.isviewall = isviewall;
        this.categoriesModels = categoriesModels;
        setupbgs();
    }

    private void setupbgs() {
        drawables[0] = getGradient(context.getResources().getColor(R.color.cat1start), context.getResources().getColor(R.color.cat1end));
        drawables[1] = getGradient(context.getResources().getColor(R.color.cat2start), context.getResources().getColor(R.color.cat2end));
        drawables[2] = getGradient(context.getResources().getColor(R.color.cat3start), context.getResources().getColor(R.color.cat3end));
        drawables[3] = getGradient(context.getResources().getColor(R.color.cat4start), context.getResources().getColor(R.color.cat4end));
        drawables[4] = getGradient(context.getResources().getColor(R.color.cat5start), context.getResources().getColor(R.color.cat5end));
    }

    private GradientDrawable getGradient(int startcolor, int endcolor) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startcolor, endcolor});
//        gd.setCornerRadius(20f);
        return gd;
    }

    public void setListner(OnAdapterItemClickListner listner) {
        this.listner = listner;
    }

    @NonNull
    @Override
    public dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_category_layout, parent, false);
        if (isviewall)
            view = layoutInflater.inflate(R.layout.adapter_category_layout_viewall, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            CategoriesModel categories = categoriesModels.get(position);
            if (!categories.islocal && !TextUtils.isEmpty(categories.getIcon())) {
                Picasso.get().load(categories.getIcon()).into(holder.image);
                holder.image.setPadding(0, 0, 0, 0);
                holder.mainlay.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
//            if (categories.islocal) {
//                holder.image.setImageResource(R.drawable.ic_all);
//                holder.image.setPadding(10,10,10,10);
//                holder.mainlay.setBackground(drawables[4]);
//            }
            holder.text.setText(categories.getName());
//            .replace(" ", "\n")
//            int random = new Random().nextInt((max - min) + 1) + min;

//            holder.mainlay.setBackground(drawables[count]);
//            holder.cardview.setOutlineAmbientShadowColor(ContextCompat.getColor(context, cardbgs[count]));
//            holder.cardview.setOutlineSpotShadowColor(ContextCompat.getColor(context, cardbgs[count]));
            count++;
            if (count >= drawables.length)
                count = 0;
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
        TextView text;
        LinearLayout mainlay;
        CardView cardview;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            mainlay = itemView.findViewById(R.id.mainlay);
//            cardview = itemView.findViewById(R.id.cardview);
        }

    }
}
