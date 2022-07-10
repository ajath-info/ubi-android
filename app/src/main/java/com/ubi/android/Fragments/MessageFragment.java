package com.ubi.android.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.activity.ChatActivity;
import com.ubi.android.activity.MessageReplyActivity;
import com.ubi.android.adapters.ProductMessageAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ProductMessageResponse;
import com.ubi.android.models.ProductMessages;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<ProductMessages> locations = new ArrayList<>();
    ProductMessageAdapter adapter;
    TextView nodata;
    UserData user;
    SwipeRefreshLayout swiperefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_fragment, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        swiperefresh = v.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        user = AppPreferences.getInstance().getUserData(getActivity());
        nodata = v.findViewById(R.id.nodata);
        adapter = new ProductMessageAdapter(getActivity(), locations, user.getId());
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                if (view.getId() == R.id.whatsappicon) {
                    String userid = locations.get(pos).getUser_id();
                    String receiverId = locations.get(pos).getVendor_id();
                    String nodeid = "";
                    if (Integer.parseInt(userid) > Integer.parseInt(receiverId)) {
                        nodeid = userid + "_" + receiverId;
                    } else {
                        nodeid = receiverId + "_" + userid;
                    }
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("receiverId", locations.get(pos).getVendor_id().equalsIgnoreCase(user.getId())
                            ? locations.get(pos).getUser_id() : locations.get(pos).getVendor_id());
                    intent.putExtra("user_id", locations.get(pos).getUser_id());
                    intent.putExtra("nodeid", nodeid);
                    intent.putExtra("receiverName", locations.get(pos).getVendor_details().first_name + " " +
                            locations.get(pos).getVendor_details().last_name);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), MessageReplyActivity.class);
                    intent.putExtra("message_id", locations.get(pos).getId());
                    intent.putExtra("vendor_id", locations.get(pos).getVendor_id());
                    startActivity(intent);
                }
            }
        });

        progressbar = v.findViewById(R.id.progressbar);
        recyclerview = v.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);
        fetchData();
    }

    private void fetchData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getProductDetails(params);
    }

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            locations.clear();
            nodata.setText("");
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<ProductMessageResponse> call = apiInterface.getProductComments(token, params);
            call.enqueue(new Callback<ProductMessageResponse>() {
                @Override
                public void onResponse(Call<ProductMessageResponse> call, Response<ProductMessageResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (getActivity() != null) {
                            swiperefresh.setRefreshing(false);
                            progressbar.setVisibility(View.GONE);
                            if (response.code() == 200) {
                                if (response.body().code == 1) {
                                    if (response.body().payload != null) {
                                        if (response.body().payload.size() > 0) {
                                            locations.addAll(response.body().payload);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            nodata.setText(response.body().message);
                                            nodata.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        nodata.setText(response.body().message);
                                        nodata.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    nodata.setText(response.body().message);
                                    nodata.setVisibility(View.VISIBLE);
                                }
                            } else {
                                AppUtils.showalert(getActivity(), response.message(), false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProductMessageResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }
}
