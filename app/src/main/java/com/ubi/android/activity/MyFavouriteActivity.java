package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.ProductInterestAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.MyFavouriteResponse;
import com.ubi.android.models.ProductInterest;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavouriteActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<ProductInterest> productInterests = new ArrayList<>();
    ProductInterestAdapter adapter;
    TextView nodata;
    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourite);
        init();
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch();
            }
        });
        updaereadlog();
        nodata = findViewById(R.id.nodata);
        adapter = new ProductInterestAdapter(this, productInterests);
        adapter.showHeart(true);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", productInterests.get(pos).getId());
                startActivity(intent);
            }
        });

        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(adapter);
        fetch();
    }

    private void fetch() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getProductDetails(params);
    }

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            productInterests.clear();
            nodata.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<MyFavouriteResponse> call = apiInterface.myFavorite(token, params);
            call.enqueue(new Callback<MyFavouriteResponse>() {
                @Override
                public void onResponse(Call<MyFavouriteResponse> call, Response<MyFavouriteResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        swiperefresh.setRefreshing(false);
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload.size() > 0) {
                                        productInterests.addAll(response.body().payload);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        nodata.setText(response.body().message);
                                        nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                                    }
                                } else {
                                    nodata.setText(response.body().message);
                                    nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                                }
                            } else {
                                nodata.setText(response.body().message);
                                nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(MyFavouriteActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MyFavouriteResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(MyFavouriteActivity.this);
        }
    }

    private void updaereadlog() {
        if (AppUtils.isConnectingToInternet(MyFavouriteActivity.this)) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(MyFavouriteActivity.this);
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "2");
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
        }
    }
}