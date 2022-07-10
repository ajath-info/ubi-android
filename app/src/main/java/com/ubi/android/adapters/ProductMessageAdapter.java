package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ProductMessages;

import java.util.ArrayList;

public class ProductMessageAdapter extends RecyclerView.Adapter<ProductMessageAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<ProductMessages> categoriesModels;
    private OnAdapterItemClickListner listner;
    String userid;


    public ProductMessageAdapter(Activity context, ArrayList<ProductMessages> categoriesModels, String userid) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.categoriesModels = categoriesModels;
        this.userid = userid;
    }


    public void setListner(OnAdapterItemClickListner listner) {
        this.listner = listner;
    }

    @NonNull
    @Override
    public dataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.productmessage_item_lay, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ProductMessages categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getProduct_icon())) {
                Picasso.get().load(categories.getProduct_icon()).into(holder.img);
            }
            holder.titletv.setText(categories.getProduct_name());
            holder.addresstxt.setText(categories.getProduct_location());
            holder.timetv.setText(categories.getCreated_time());
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });
            holder.callappicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CALL_PHONE},
                                    100);
                        } else
                            calluser(categories.getVendor_details().getPhone());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.whatsappicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });
//            if (userid.equalsIgnoreCase(categories.getVendor_id())) {
//                holder.whatsappicon.setVisibility(View.GONE);
//            } else {
//                holder.whatsappicon.setVisibility(View.VISIBLE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void calluser(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    static class dataViewHolder extends RecyclerView.ViewHolder {
        TextView titletv, addresstxt, timetv;
        LinearLayout mainlay;
        ImageView img, callappicon, whatsappicon;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            timetv = itemView.findViewById(R.id.timetv);
            mainlay = itemView.findViewById(R.id.mainlay);
            addresstxt = itemView.findViewById(R.id.addresstxt);
            titletv = itemView.findViewById(R.id.titletv);
            callappicon = itemView.findViewById(R.id.callappicon);
            whatsappicon = itemView.findViewById(R.id.whatsappicon);
        }

    }
}
