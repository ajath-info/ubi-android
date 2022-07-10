package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chaek.android.RatingBar;
import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.activity.ProductBestQuoteActivity;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.SubCategoryDetailModel;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<SubCategoryDetailModel> categoriesModels;
    private OnAdapterItemClickListner listner;


    public SubCategoryAdapter(Activity context, ArrayList<SubCategoryDetailModel> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_subcat_adapter_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            SubCategoryDetailModel categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.image)) {
                Picasso.get().load(categories.image).into(holder.image);
            }
            if (categories.is_fav.equalsIgnoreCase("true"))
                holder.hearticon.setImageResource(R.drawable.ic_heart_selected);
            else
                holder.hearticon.setImageResource(R.drawable.ic_heart_unselected);
            holder.titletv.setText(Html.fromHtml(categories.name));
            holder.pricetv.setText("$" + categories.price);
            holder.locationtv.setText(Html.fromHtml(categories.location));
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });
            if (!TextUtils.isEmpty(categories.totla_rating))
                holder.sellerrating.setScore((int) Double.parseDouble(categories.avg_rating));
            holder.sellerratingtv.setText(categories.avg_rating);
            holder.sellertotalrating.setText("(" + categories.totla_rating + ")");
            holder.distancetv.setText("Distance " + categories.distance + "Km");
            holder.bestdeallay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, ProductBestQuoteActivity.class);
                        intent.putExtra("name", categories.name);
                        intent.putExtra("vendorname", categories.vendor_details.first_name);
                        intent.putExtra("mobile", categories.vendor_details.getPhone());
                        intent.putExtra("image", categories.image);
                        intent.putExtra("category_id", categories.category_id);
                        intent.putExtra("sub_category_id", categories.sub_category_id);
                        intent.putExtra("product_id", categories.id);
                        intent.putExtra("vendor_id", categories.vendor_details.vendor_id);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.callnow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (categories.is_payment.equalsIgnoreCase("true")) {
                            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CALL_PHONE},
                                        100);
                            } else
                                calluser(categories.vendor_details.getPhone());
                        } else {
                            AppUtils.showalert(context, "You can't make a call to this vendor", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (categories.is_payment.equalsIgnoreCase("true"))
                holder.locationlay.setVisibility(View.VISIBLE);
            else
                holder.locationlay.setVisibility(View.GONE);

            holder.hearticon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

            holder.sellerrating.setOnClickListener(new View.OnClickListener() {
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
        TextView titletv, distancetv, pricetv, locationtv, sellerratingtv, sellertotalrating;
        LinearLayout mainlay, locationlay;
        RelativeLayout callnow, bestdeallay;
        //        ImageView whatsappicon, callappicon;
        RatingBar sellerrating;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            locationlay = itemView.findViewById(R.id.locationlay);
            bestdeallay = itemView.findViewById(R.id.bestdeallay);
            hearticon = itemView.findViewById(R.id.hearticon);
            mainlay = itemView.findViewById(R.id.mainlay);
            image = itemView.findViewById(R.id.image);
            pricetv = itemView.findViewById(R.id.pricetv);
            titletv = itemView.findViewById(R.id.titletv);
            locationtv = itemView.findViewById(R.id.locationtv);
            callnow = itemView.findViewById(R.id.callnow);
            sellerrating = itemView.findViewById(R.id.sellerrating);
            sellerratingtv = itemView.findViewById(R.id.sellerratingtv);
            sellertotalrating = itemView.findViewById(R.id.sellertotalrating);
            distancetv = itemView.findViewById(R.id.distancetv);
//            whatsappicon = itemView.findViewById(R.id.whatsappicon);
//            callappicon = itemView.findViewById(R.id.callappicon);
        }

    }
}
