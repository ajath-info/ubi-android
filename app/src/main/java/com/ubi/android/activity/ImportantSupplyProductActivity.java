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
import com.ubi.android.adapters.ImportantSuppliesProductAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ImportantSupplyProductBean;
import com.ubi.android.models.ImportantSupplyProductResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportantSupplyProductActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ImportantSuppliesProductAdapter productInterestAdapter;
    ArrayList<ImportantSupplyProductBean> data = new ArrayList<>();
    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_supply_product);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        productInterestAdapter = new ImportantSuppliesProductAdapter(ImportantSupplyProductActivity.this, data);
        productInterestAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(ImportantSupplyProductActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", data.get(pos).getId());
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(productInterestAdapter);

        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText(getIntent().getStringExtra("title"));

        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch();
            }
        });
        fetch();
    }

    private void fetch() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        params.put("important_supply_id", getIntent().getStringExtra("important_supply_id"));
        getHomeData(params);
    }

    private void getHomeData(Map<String, String> params) {
        data.clear();
        productInterestAdapter.notifyDataSetChanged();
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<ImportantSupplyProductResponse> call = apiInterface.getImportantSuppliesAll(token, params);
            call.enqueue(new Callback<ImportantSupplyProductResponse>() {
                @Override
                public void onResponse(Call<ImportantSupplyProductResponse> call, Response<ImportantSupplyProductResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        swiperefresh.setRefreshing(false);
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    data.addAll(response.body().payload);
                                    productInterestAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(ImportantSupplyProductActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ImportantSupplyProductActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ImportantSupplyProductResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ImportantSupplyProductActivity.this);
        }
    }
}