package com.ubi.android.Fragments;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.NotificationAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.NotificationModel;
import com.ubi.android.models.NotificationReponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<NotificationModel> locations = new ArrayList<>();
    NotificationAdapter adapter;
    TextView nodata;
    SwipeRefreshLayout swiperefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_fragment, container, false);
        init(v);
        return v;
    }

    public void deleteall() {
        deleteallnotification();
    }

    private void init(View v) {
        swiperefresh = v.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchnotification();
            }
        });
        updaereadlog();
        nodata = v.findViewById(R.id.nodata);
        adapter = new NotificationAdapter(getActivity(), locations);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {

            }
        });

        progressbar = v.findViewById(R.id.progressbar);
        recyclerview = v.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);
        fetchnotification();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);
    }

    private void fetchnotification() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getProductDetails(params);
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            Toast.makeText(ListActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
//            locations.remove(position);
//            adapter.notifyDataSetChanged();
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", locations.get(position).getId());
            delete(params);
        }
    };

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            locations.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<NotificationReponse> call = apiInterface.getNotificationLog(token, params);
            call.enqueue(new Callback<NotificationReponse>() {
                @Override
                public void onResponse(Call<NotificationReponse> call, Response<NotificationReponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (getActivity() != null) {
                            swiperefresh.setRefreshing(false);
                            progressbar.setVisibility(View.GONE);
                            if (response.code() == 200) {
                                if (response.body().code == 1) {
                                    if (response.body().payload != null) {
                                        if (response.body().payload != null) {
                                            locations.addAll(response.body().payload);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    nodata.setText(response.body().message);
                                    nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
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
                public void onFailure(Call<NotificationReponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }

    private void delete(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<BaseResponse> call = apiInterface.deleteNotification(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            fetchnotification();
                            AppUtils.showalert(getActivity(), response.body().message, false);
                        } else {
                            AppUtils.showalert(getActivity(), response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }

    private void deleteallnotification() {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Map<String, String> params = new HashMap<String, String>();
            Call<BaseResponse> call = apiInterface.deleteNotificationLog(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            fetchnotification();
                            AppUtils.showalert(getActivity(), response.body().message, false);
                        } else {
                            AppUtils.showalert(getActivity(), response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }

    private void updaereadlog() {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Map<String, String> params = new HashMap<String, String>();
            params.put("type","1");
            Call<BaseResponse> call = apiInterface.readLog(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }
}
