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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.PostSubCatAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.CategoriesModel;
import com.ubi.android.models.CategoriesResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostSubCatActivity extends AppCompatActivity {

    TextView locationtv;
    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<CategoriesModel> locations = new ArrayList<>();
    PostSubCatAdapter adapter;
    TextView nodata;
    TextView categorytxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adpostsubcat);
        init();
    }

    private void init() {
        categorytxt = findViewById(R.id.categorytxt);
        categorytxt.setText(getIntent().getStringExtra("name"));
        locationtv = findViewById(R.id.locationtv);
        nodata = findViewById(R.id.nodata);
        adapter = new PostSubCatAdapter(this, locations);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(PostSubCatActivity.this, PostAdFirstStepActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                intent.putExtra("sub_category_id", locations.get(pos).getId());
                startActivityForResult(intent, 101);
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
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(adapter);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        params.put("category_id", getIntent().getStringExtra("category_id"));
        getProductDetails(params);
    }

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            locations.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<CategoriesResponse> call = apiInterface.subCategory(token, params);
            call.enqueue(new Callback<CategoriesResponse>() {
                @Override
                public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
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
                            AppUtils.showalert(PostSubCatActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(PostSubCatActivity.this);
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