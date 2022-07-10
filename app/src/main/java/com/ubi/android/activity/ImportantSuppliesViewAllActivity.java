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

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.SupplierAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ImportantSupplies;
import com.ubi.android.models.SuppliesResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportantSuppliesViewAllActivity extends AppCompatActivity {
    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<ImportantSupplies> supplies = new ArrayList<>();
    SupplierAdapter supplierAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_supplies_view_all);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        supplierAdapter = new SupplierAdapter(this, supplies);
        supplierAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(ImportantSuppliesViewAllActivity.this, ImportantSupplyProductActivity.class);
                intent.putExtra("important_supply_id", supplies.get(pos).getId());
                intent.putExtra("title", supplies.get(pos).getName());
                startActivity(intent);
            }
        });
        supplierAdapter.isviewall = true;
        recyclerview.setAdapter(supplierAdapter);
        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Important Supplies");
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        if (getIntent().hasExtra("category_id")) {
            params.put("category_id", getIntent().getStringExtra("category_id"));
            getCatData(params);
        } else {
            getHomeData(params);
        }

    }

    private void getHomeData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<SuppliesResponse> call = apiInterface.suppliesAll(token, params);
            call.enqueue(new Callback<SuppliesResponse>() {
                @Override
                public void onResponse(Call<SuppliesResponse> call, Response<SuppliesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    supplies.addAll(response.body().payload);
                                    supplierAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(ImportantSuppliesViewAllActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ImportantSuppliesViewAllActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SuppliesResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ImportantSuppliesViewAllActivity.this);
        }
    }

    private void getCatData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<SuppliesResponse> call = apiInterface.categorSupplyAll(token, params);
            call.enqueue(new Callback<SuppliesResponse>() {
                @Override
                public void onResponse(Call<SuppliesResponse> call, Response<SuppliesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    supplies.addAll(response.body().payload);
                                    supplierAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(ImportantSuppliesViewAllActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ImportantSuppliesViewAllActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SuppliesResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ImportantSuppliesViewAllActivity.this);
        }
    }
}