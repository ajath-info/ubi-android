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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.SearchData;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<SearchData> categoriesModels;
    private OnAdapterItemClickListner listner;


    public SearchAdapter(Activity context, ArrayList<SearchData> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_search_adapter_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            SearchData categories = categoriesModels.get(position);
//            if (!TextUtils.isEmpty(categories.getImage())) {
//                Picasso.get().load(categories.getImage()).into(holder.image);
//            }
            holder.titletv.setText(categories.getName());
//            holder.pricetv.setText("$" + categories.getPrice());
//            holder.locationtv.setText(categories.getLocation());
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

//            holder.callappicon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    try {
////                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
////                            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CALL_PHONE},
////                                    100);
////                        } else
////                            calluser(categories.getVendor_details().getPhone());
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//                }
//            });
//            if (!TextUtils.isEmpty(categories.is_fav) && categories.is_fav.equalsIgnoreCase("true"))
//                holder.hearticon.setImageResource(R.drawable.ic_heart_selected);
//            else
//                holder.hearticon.setImageResource(R.drawable.ic_heart_unselected);
//
//            holder.hearticon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listner != null)
//                        listner.onClick(view, position);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClickWhatsApp(String number) {
        PackageManager pm = context.getPackageManager();
        try {
//            Intent waIntent = new Intent(Intent.ACTION_SEND);
//            waIntent.setType("text/plain");
//            String text = "Hi";
//            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//            waIntent.setPackage("com.whatsapp");
//            waIntent.putExtra(Intent.EXTRA_TEXT, text);
//            context.startActivity(Intent.createChooser(waIntent, "Share with"));

            Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            context.startActivity(Intent.createChooser(i, ""));

        } catch (Exception e) {
            Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
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
        ImageView image, hearticon;
        TextView titletv, pricetv, locationtv;
        LinearLayout mainlay;
//        ImageView whatsappicon, callappicon;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            hearticon = itemView.findViewById(R.id.hearticon);
            mainlay = itemView.findViewById(R.id.mainlay);
            image = itemView.findViewById(R.id.image);
            pricetv = itemView.findViewById(R.id.pricetv);
            titletv = itemView.findViewById(R.id.titletv);
            locationtv = itemView.findViewById(R.id.locationtv);
//            whatsappicon = itemView.findViewById(R.id.whatsappicon);
//            callappicon = itemView.findViewById(R.id.callappicon);
        }

    }
}
