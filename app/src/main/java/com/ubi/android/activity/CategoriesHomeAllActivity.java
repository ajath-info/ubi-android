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
import com.ubi.android.adapters.CategoryHomeAdapter;
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

public class CategoriesHomeAllActivity extends AppCompatActivity {

    RecyclerView catrecyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<CategoriesModel> categoriesModels = new ArrayList<>();
    CategoryHomeAdapter categoryHomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_view_all);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Categories");
        progressbar = findViewById(R.id.progressbar);
        catrecyclerview = findViewById(R.id.catrecyclerview);
        catrecyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        categoryHomeAdapter = new CategoryHomeAdapter(this, categoriesModels, true);
        categoryHomeAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(CategoriesHomeAllActivity.this, CategoryDetailActivity.class);
                intent.putExtra("category_id", categoriesModels.get(pos).getId());
                intent.putExtra("title", categoriesModels.get(pos).getName());
                startActivity(intent);
            }
        });
        catrecyclerview.setAdapter(categoryHomeAdapter);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getHomeData(params);
    }

    private void getHomeData(Map<String, String> params) {
        categoriesModels.clear();
        categoryHomeAdapter.notifyDataSetChanged();

        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<CategoriesResponse> call = apiInterface.categoriesAll(token, params);
            call.enqueue(new Callback<CategoriesResponse>() {
                @Override
                public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    categoriesModels.addAll(response.body().payload);
                                    categoryHomeAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(CategoriesHomeAllActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(CategoriesHomeAllActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(CategoriesHomeAllActivity.this);
        }
    }
}