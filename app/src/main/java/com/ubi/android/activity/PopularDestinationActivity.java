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
import com.ubi.android.adapters.PopularDestinationAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.PopularDestinationModel;
import com.ubi.android.models.PopularDestinationResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularDestinationActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<PopularDestinationModel> supplies = new ArrayList<>();
    PopularDestinationAdapter supplierAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_destination);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        supplierAdapter = new PopularDestinationAdapter(this, supplies);
        supplierAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(PopularDestinationActivity.this, ProductListActivity.class);
                intent.putExtra("state_id", supplies.get(pos).state_id);
                intent.putExtra("title", supplies.get(pos).name);
                intent.putExtra("type", "populardestination");
                startActivity(intent);
            }
        });
        supplierAdapter.viewall = true;
        recyclerview.setAdapter(supplierAdapter);
        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
//        toptitletv.setText("Important Supplies");
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        params.put("category_id", getIntent().getStringExtra("category_id"));
        getHomeData(params);
    }

    private void getHomeData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<PopularDestinationResponse> call = apiInterface.popularDestinationAll(token, params);
            call.enqueue(new Callback<PopularDestinationResponse>() {
                @Override
                public void onResponse(Call<PopularDestinationResponse> call, Response<PopularDestinationResponse> response) {
                    Log.d("", response.code() + ""+response.raw().toString());
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    supplies.addAll(response.body().payload);
                                    supplierAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(PopularDestinationActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(PopularDestinationActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PopularDestinationResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(PopularDestinationActivity.this);
        }
    }
}