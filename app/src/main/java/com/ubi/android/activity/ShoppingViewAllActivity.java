package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.ShoppingAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.ShoppingModel;
import com.ubi.android.models.ShoppingResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingViewAllActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ShoppingAdapter shoppingAdapter;
    ArrayList<ShoppingModel> shoppingModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_view_all);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        shoppingAdapter = new ShoppingAdapter(this, shoppingModels);
        shoppingAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(ShoppingViewAllActivity.this, ProductListActivity.class);
                intent.putExtra("shopping_id", shoppingModels.get(pos).getId());
                intent.putExtra("title", shoppingModels.get(pos).getName());
                intent.putExtra("type", "shopping");
                startActivity(intent);
            }
        });
        shoppingAdapter.isviewall = true;
        recyclerview.setAdapter(shoppingAdapter);
        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Shopping");
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
            Call<ShoppingResponse> call = apiInterface.shoppingAll(token, params);
            call.enqueue(new Callback<ShoppingResponse>() {
                @Override
                public void onResponse(Call<ShoppingResponse> call, Response<ShoppingResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    shoppingModels.addAll(response.body().payload);
                                    shoppingAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(ShoppingViewAllActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ShoppingViewAllActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ShoppingResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ShoppingViewAllActivity.this);
        }
    }
}