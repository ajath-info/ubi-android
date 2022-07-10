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
import com.ubi.android.adapters.AutoMobileAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.AutoMobileResponse;
import com.ubi.android.models.AutomobileModel;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoMobileViewallActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<AutomobileModel> automobileModels = new ArrayList<>();
    AutoMobileAdapter autoMobileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_mobile_viewall);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        autoMobileAdapter = new AutoMobileAdapter(this, automobileModels);
        autoMobileAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(AutoMobileViewallActivity.this, ProductListActivity.class);
                intent.putExtra("automobile_id", automobileModels.get(pos).getId());
                intent.putExtra("title", automobileModels.get(pos).getName());
                intent.putExtra("type", "automobile");
                startActivity(intent);
            }
        });
        autoMobileAdapter.isviewall = true;
        recyclerview.setAdapter(autoMobileAdapter);
        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Automobile");
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        getHomeData(params);
    }

    private void getHomeData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<AutoMobileResponse> call = apiInterface.automobileAll(token, params);
            call.enqueue(new Callback<AutoMobileResponse>() {
                @Override
                public void onResponse(Call<AutoMobileResponse> call, Response<AutoMobileResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    automobileModels.addAll(response.body().payload);
                                    autoMobileAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(AutoMobileViewallActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(AutoMobileViewallActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AutoMobileResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(AutoMobileViewallActivity.this);
        }
    }
}