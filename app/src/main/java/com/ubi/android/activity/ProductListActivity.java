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

public class ProductListActivity extends AppCompatActivity {

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
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch();
            }
        });
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        productInterestAdapter = new ImportantSuppliesProductAdapter(ProductListActivity.this, data);
        productInterestAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", data.get(pos).getId());
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(productInterestAdapter);

        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText(getIntent().getStringExtra("title"));
        fetch();

    }

    private void fetch() {
        String type = getIntent().getStringExtra("type");
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");

        if (type.equalsIgnoreCase("shopping"))
            params.put("shopping_id", getIntent().getStringExtra("shopping_id"));
        else if (type.equalsIgnoreCase("hotel"))
            params.put("hotel_stay_id", getIntent().getStringExtra("hotel_stay_id"));
        else if (type.equalsIgnoreCase("automobile"))
            params.put("automobile_id", getIntent().getStringExtra("automobile_id"));
        else if (type.equalsIgnoreCase("populardestination"))
            params.put("state_id", getIntent().getStringExtra("state_id"));

        getHomeData(params, type);
    }

    private void getHomeData(Map<String, String> params, String type) {
        data.clear();
        productInterestAdapter.notifyDataSetChanged();
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<ImportantSupplyProductResponse> call = null;
            if (type.equalsIgnoreCase("shopping")) {
                call = apiInterface.shoppingProducts(token, params);

            } else if (type.equalsIgnoreCase("hotel")) {
                call = apiInterface.hotelStayProducts(token, params);

            } else if (type.equalsIgnoreCase("automobile"))
                call = apiInterface.automobileProducts(token, params);

            else if (type.equalsIgnoreCase("populardestination"))
                call = apiInterface.popularDestinationsProducts(token, params);

            call.enqueue(new Callback<ImportantSupplyProductResponse>() {
                @Override
                public void onResponse(Call<ImportantSupplyProductResponse> call, Response<ImportantSupplyProductResponse> response) {
                    Log.d(ProductListActivity.class.getName(), response.code() + "");
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
                                AppUtils.showalert(ProductListActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ProductListActivity.this, response.message(), true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ImportantSupplyProductResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ProductListActivity.this);
        }
    }
}