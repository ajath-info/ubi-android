package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.ubi.android.R;
import com.ubi.android.activity.ChatActivity;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.MyLeads;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;

import java.util.ArrayList;

public class MyLeadsAdapter extends RecyclerView.Adapter<MyLeadsAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<MyLeads> categoriesModels;
    private OnAdapterItemClickListner listner;
    UserData userData;

    public MyLeadsAdapter(Activity context, ArrayList<MyLeads> categoriesModels) {
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
        View view = layoutInflater.inflate(R.layout.adapter_myleads_layout, parent, false);
        return new dataViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            MyLeads categories = categoriesModels.get(position);
            holder.username.setText(categories.getUser_details().getUser_name());
            holder.mobiletv.setText(categories.getUser_details().getUser_phone());
            holder.addresstv.setText(categories.getProduct_location());
            holder.locationtv.setText("I am interested in " + categories.getProduct_name());
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

            holder.whatsappicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String userid = userData.getId();
                        String receiverId = categories.getUser_details().getUser_id();
                        String nodeid = "";
                        if (Integer.parseInt(userid) > Integer.parseInt(receiverId)) {
                            nodeid = userid + "_" + receiverId;
                        } else {
                            nodeid = receiverId + "_" + userid;
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("receiverId", categories.getUser_details().getUser_id().equalsIgnoreCase(userData.getId())
                                ? userData.getId() : categories.getUser_details().getUser_id());
                        intent.putExtra("user_id", userData.getId());
                        intent.putExtra("nodeid", nodeid);
                        intent.putExtra("receiverName", categories.getUser_details().getUser_name());
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
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CALL_PHONE},
                                    100);
                        } else
                            calluser(categories.getUser_details().getUser_phone());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        ImageView whatsappicon, callappicon;
        TextView username, mobiletv, addresstv, locationtv, sharecatlog;
        LinearLayout mainlay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            locationtv = itemView.findViewById(R.id.locationtv);
            whatsappicon = itemView.findViewById(R.id.whatsappicon);
            callappicon = itemView.findViewById(R.id.callappicon);
            mobiletv = itemView.findViewById(R.id.mobiletv);
            username = itemView.findViewById(R.id.username);
            addresstv = itemView.findViewById(R.id.addresstv);
            sharecatlog = itemView.findViewById(R.id.sharecatlog);
            mainlay = itemView.findViewById(R.id.mainlay);
        }

    }
}
