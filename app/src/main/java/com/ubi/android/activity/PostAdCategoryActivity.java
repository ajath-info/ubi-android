package com.ubi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.PostAdCategoryAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.CategoriesModel;
import com.ubi.android.models.CategoriesResponse;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<CategoriesModel> dealsModels = new ArrayList<>();
    PostAdCategoryAdapter dealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_category);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Post Ad");
        progressbar = findViewById(R.id.progressbar);
        dealAdapter = new PostAdCategoryAdapter(this, dealsModels);
        dealAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getApplicationContext(), PostSubCatActivity.class);
                intent.putExtra("category_id", dealsModels.get(pos).getId());
                intent.putExtra("name", dealsModels.get(pos).getName());
                startActivityForResult(intent, 101);
            }
        });
        recyclerview.setAdapter(dealAdapter);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        getHomeData(params);
    }

    private void getHomeData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
//            String token = AppPreferences.getInstance().getToken(this);
            Call<CategoriesResponse> call = apiInterface.category();
            call.enqueue(new Callback<CategoriesResponse>() {
                @Override
                public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    dealsModels.addAll(response.body().payload);
                                    dealAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(PostAdCategoryActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(PostAdCategoryActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(PostAdCategoryActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}