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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ubi.android.R;
import com.ubi.android.activity.ProductBestQuoteActivity;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ImportantSupplyProductBean;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;

public class ImportantSuppliesProductAdapter extends RecyclerView.Adapter<ImportantSuppliesProductAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<ImportantSupplyProductBean> categoriesModels;
    private OnAdapterItemClickListner listner;
    boolean show = true;
    UserData userData;

    public void showHeart(boolean show) {
        this.show = show;
    }

    public ImportantSuppliesProductAdapter(Activity context, ArrayList<ImportantSupplyProductBean> categoriesModels) {
        userData = AppPreferences.getInstance().getUserData(context);
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
        View view = layoutInflater.inflate(R.layout.adapter_productinterest_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ImportantSupplyProductBean categories = categoriesModels.get(position);
            if (!TextUtils.isEmpty(categories.getImage())) {
                Picasso.get().load(categories.getImage()).into(holder.image);
            }

            if (categories.getIs_fav().equalsIgnoreCase("true"))
                holder.hearticon.setVisibility(View.VISIBLE);
            else
                holder.hearticon.setVisibility(View.GONE);

            holder.titletv.setText(categories.getName());
            holder.pricetv.setText("$" + categories.getPrice());
            holder.locationtv.setText(categories.getLocation());
            holder.whatsappicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (!TextUtils.isEmpty(categories.getVendor_details().getPhone()))
//                            onClickWhatsApp(categories.getVendor_details().getPhone());
//                        String userid = userData.getId();
//                        String receiverId = categories.getVendor_details().vendor_id;
//                        String nodeid = "";
//                        if (Integer.parseInt(userid) > Integer.parseInt(receiverId)) {
//                            nodeid = userid + "_" + receiverId;
//                        } else {
//                            nodeid = receiverId + "_" + userid;
//                        }
//                        Intent intent = new Intent(context, ChatActivity.class);
//                        intent.putExtra("receiverId", categories.getVendor_details().vendor_id.equalsIgnoreCase(userData.getId())
//                                ? userData.getId() : categories.getVendor_details().vendor_id);
//                        intent.putExtra("user_id", userData.getId());
//                        intent.putExtra("nodeid", nodeid);
//                        intent.putExtra("receiverName", categories.getVendor_details().first_name + " " +
//                                categories.getVendor_details().last_name);
//                        context.startActivity(intent);

                        Intent intent = new Intent(context, ProductBestQuoteActivity.class);
                        intent.putExtra("name", categories.getName());
                        intent.putExtra("vendorname", categories.getVendor_details().first_name + " " +
                                categories.getVendor_details().last_name);
                        intent.putExtra("mobile", categories.getVendor_details().getPhone());
                        if (!TextUtils.isEmpty(categories.getImage()))
                            intent.putExtra("image", categories.getImage());
                        intent.putExtra("category_id", categories.getCategory_id());
                        intent.putExtra("sub_category_id", categories.getSub_category_id());
                        intent.putExtra("product_id", categories.getId());
                        intent.putExtra("vendor_id", categories.getVendor_details().vendor_id);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.callappicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(categories.is_payment) && categories.is_payment.equalsIgnoreCase("true")) {
                            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CALL_PHONE},
                                        100);
                            } else
                                calluser(categories.getVendor_details().getPhone());
                        } else {
                            AppUtils.showalert(context, "You can't make call to this dealer", false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

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

    public static class dataViewHolder extends RecyclerView.ViewHolder {
        ImageView image, hearticon;
        TextView titletv, pricetv, locationtv;
        ImageView whatsappicon, callappicon;
        LinearLayout mainlay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            hearticon = itemView.findViewById(R.id.hearticon);
            callappicon = itemView.findViewById(R.id.callappicon);
            image = itemView.findViewById(R.id.image);
            pricetv = itemView.findViewById(R.id.pricetv);
            titletv = itemView.findViewById(R.id.titletv);
            locationtv = itemView.findViewById(R.id.locationtv);
            whatsappicon = itemView.findViewById(R.id.whatsappicon);
            mainlay = itemView.findViewById(R.id.mainlay);
        }

    }
}
