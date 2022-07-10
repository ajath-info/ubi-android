package com.ubi.android.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.R;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ChatRequestModel;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.dataViewHolder> {
    Activity context;
    LayoutInflater layoutInflater;
    ArrayList<ChatRequestModel> categoriesModels;
    private OnAdapterItemClickListner listner;
    String userid;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;

    public ChatAdapter(Activity context, ArrayList<ChatRequestModel> categoriesModels, String userid) {
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
        if (viewType == TYPE_ONE) {
            View view = layoutInflater.inflate(R.layout.chat_item_out_lay, parent, false);
            return new dataViewHolder(view, context);
        } else if (viewType == TYPE_TWO) {
            View view = layoutInflater.inflate(R.layout.chat_item_lay, parent, false);
            return new dataViewHolder(view, context);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatRequestModel item = categoriesModels.get(position);
        if (userid.equalsIgnoreCase(item.senderId)) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull dataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ChatRequestModel categories = categoriesModels.get(position);
            holder.titletv.setText(categories.senderName);
            holder.addresstxt.setText(categories.message);
            holder.timetv.setText(AppUtils.convertDate(categories.timeStamp));
            holder.mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null)
                        listner.onClick(view, position);
                }
            });

//            if (userid.equalsIgnoreCase(categories.senderId)) {
////                holder.chatbglay.setBackgroundResource(R.drawable.outgoingmessage);
//                holder.titletv.setTextColor(context.getResources().getColor(R.color.white));
//                holder.addresstxt.setTextColor(context.getResources().getColor(R.color.white));
//                holder.timetv.setTextColor(context.getResources().getColor(R.color.white40));
//            } else {
////                holder.chatbglay.setBackgroundResource(R.drawable.incomingspeech);
//                holder.titletv.setTextColor(context.getResources().getColor(R.color.black));
//                holder.addresstxt.setTextColor(context.getResources().getColor(R.color.black));
//                holder.timetv.setTextColor(context.getResources().getColor(R.color.black_40));
//            }

            holder.titletv.setTextColor(context.getResources().getColor(R.color.black));
            holder.addresstxt.setTextColor(context.getResources().getColor(R.color.black));
            holder.timetv.setTextColor(context.getResources().getColor(R.color.black_40));
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
        LinearLayout mainlay, chatbglay;

        public dataViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            chatbglay = itemView.findViewById(R.id.chatbglay);
            timetv = itemView.findViewById(R.id.timetv);
            mainlay = itemView.findViewById(R.id.mainlay);
            addresstxt = itemView.findViewById(R.id.addresstxt);
            titletv = itemView.findViewById(R.id.titletv);
        }

    }
}
