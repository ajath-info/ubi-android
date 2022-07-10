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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.DealsModel;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<DealsModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    int count = 0;
    int width = 0;
    public boolean isviewall = false;
    int bgs[] = {R.drawable.dealbgone, R.drawable.dealbgtwo,
            R.drawable.dealbgthree,
            R.drawable.dealbgfour, R.drawable.dealbgfive,
            R.drawable.dealbgsix};

    public DealAdapter(Activity context, ArrayList<DealsModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_deal_layout, parent, false);
        if (isviewall)
            view = layoutInflater.inflate(R.layout.adapter_deal_layout_viewall, parent, false);

        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            DealsModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getIcon())) {
                Picasso.get().load(categories.getIcon()).into(holder.image);
            }
//            int random = new Random().nextInt((max - min) + 1) + min;
            holder.mainlay.setBackgroundResource(bgs[count]);
//            holder.text.setText(categories.getName());
            count++;
            if (count >= bgs.length)
                count = 0;
            if (isviewall) {
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (width / 3));
                holder.toplay.setLayoutParams(params);
            }
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
        RelativeLayout mainlay;
        LinearLayout toplay;
//        TextView text;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            toplay = itemView.findViewById(R.id.toplay);
            mainlay = itemView.findViewById(R.id.mainlay);
//            text = itemView.findViewById(R.id.text);
        }

    }
}
