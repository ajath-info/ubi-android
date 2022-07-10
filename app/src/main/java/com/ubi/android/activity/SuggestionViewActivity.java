package com.ubi.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.HotelSuggestionAdapter;
import com.ubi.android.adapters.PopularDestinationAdapter;
import com.ubi.android.models.PopularDestinationModel;
import com.ubi.android.models.PopularDestinationResponse;
import com.ubi.android.models.SuggestionModel;
import com.ubi.android.models.SuggestionResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionViewActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<SuggestionModel> suggestionModels = new ArrayList<>();
    HotelSuggestionAdapter hotelSuggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_view);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        hotelSuggestionAdapter = new HotelSuggestionAdapter(this, suggestionModels);
        hotelSuggestionAdapter.viewall = true;
        recyclerview.setAdapter(hotelSuggestionAdapter);
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
            Call<SuggestionResponse> call = apiInterface.suggestionAll(token, params);
            call.enqueue(new Callback<SuggestionResponse>() {
                @Override
                public void onResponse(Call<SuggestionResponse> call, Response<SuggestionResponse> response) {
                    Log.d("", response.code() + ""+response.raw().toString());
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    suggestionModels.addAll(response.body().payload);
                                    hotelSuggestionAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(SuggestionViewActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(SuggestionViewActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SuggestionResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(SuggestionViewActivity.this);
        }
    }
}