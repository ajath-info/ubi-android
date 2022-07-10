package com.ubi.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.MessageRepliesAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.MessageRepliesModel;
import com.ubi.android.models.MessageRepliesResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.models.UserResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageReplyActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<MessageRepliesModel> locations = new ArrayList<>();
    MessageRepliesAdapter adapter;
    TextView nodata;
    SwipeRefreshLayout swipe;
    EditText messagtv;
    LinearLayout replylay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reply);
        replylay = findViewById(R.id.replylay);
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchdata();
            }
        });
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nodata = findViewById(R.id.nodata);
        adapter = new MessageRepliesAdapter(this, locations);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {

            }
        });

        progressbar = findViewById(R.id.progressbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        fetchdata();
        messagtv = findViewById(R.id.messagtv);
        findViewById(R.id.sendlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strmessagtv = messagtv.getText().toString();
                if (TextUtils.isEmpty(strmessagtv)) {
                    AppUtils.showalert(MessageReplyActivity.this, "Please enter your reply message", false);
                } else {
                    AppUtils.hidekeyboard(MessageReplyActivity.this, messagtv);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("reply_to", "1");
                    params.put("message", strmessagtv);
                    params.put("message_id", getIntent().getStringExtra("message_id"));
                    submitReply(params);
                }
            }
        });
        Map<String, String> params = new HashMap<String, String>();
        getUserDetail(params);

        UserData data = AppPreferences.getInstance().getUserData(getApplicationContext());
        if (data != null) {
            if (!TextUtils.isEmpty(data.getIs_vendor()) && data.getIs_vendor().equalsIgnoreCase("true")
                    && data.getId().equalsIgnoreCase(getIntent().getStringExtra("vendor_id")))
                replylay.setVisibility(View.VISIBLE);
        }
    }

    private void fetchdata() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        params.put("message_id", getIntent().getStringExtra("message_id"));
        getProductDetails(params);
    }

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(MessageReplyActivity.this)) {
            nodata.setVisibility(View.GONE);
            locations.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            swipe.setRefreshing(true);

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(MessageReplyActivity.this);
            Call<MessageRepliesResponse> call = apiInterface.getProductReply(token, params);
            call.enqueue(new Callback<MessageRepliesResponse>() {
                @Override
                public void onResponse(Call<MessageRepliesResponse> call, Response<MessageRepliesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        swipe.setRefreshing(false);
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
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(MessageReplyActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MessageRepliesResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                }
            });
        } else {
            AppUtils.Nointernetalert(MessageReplyActivity.this);
        }
    }

    private void submitReply(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(MessageReplyActivity.this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(MessageReplyActivity.this);
            Call<BaseResponse> call = apiInterface.productReply(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                messagtv.setText("");
                                fetchdata();
                                AppUtils.showalert(MessageReplyActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(MessageReplyActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(MessageReplyActivity.this);
        }
    }

    private void getUserDetail(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(MessageReplyActivity.this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(MessageReplyActivity.this);
            Call<UserResponse> call = apiInterface.myProfile(token, params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                try {
                                    if (response.body().payload.is_vendor.equalsIgnoreCase("true")
                                            && response.body().payload.getId().equalsIgnoreCase(getIntent().getStringExtra("vendor_id")))
                                        replylay.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            AppUtils.showalert(MessageReplyActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(MessageReplyActivity.this);
        }
    }
}