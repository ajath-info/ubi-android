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
import com.ubi.android.adapters.HotelStayAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.HotelStay;
import com.ubi.android.models.HotelStayResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelStayViewAllActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    TextView toptitletv;
    ArrayList<HotelStay> hotelStays = new ArrayList<>();
    HotelStayAdapter hotelStayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_stay_view_all);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        hotelStayAdapter = new HotelStayAdapter(this, hotelStays);
        hotelStayAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(HotelStayViewAllActivity.this, ProductListActivity.class);
                intent.putExtra("hotel_stay_id", hotelStays.get(pos).getId());
                intent.putExtra("title", hotelStays.get(pos).getName());
                intent.putExtra("type", "hotel");
                startActivity(intent);
            }
        });
        hotelStayAdapter.isviewall = true;
        recyclerview.setAdapter(hotelStayAdapter);
        progressbar = findViewById(R.id.progressbar);
        toptitletv = findViewById(R.id.toptitletv);
        toptitletv.setText("Hotel & Stay");
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
            Call<HotelStayResponse> call = apiInterface.hotelStayAll(token, params);
            call.enqueue(new Callback<HotelStayResponse>() {
                @Override
                public void onResponse(Call<HotelStayResponse> call, Response<HotelStayResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    hotelStays.addAll(response.body().payload);
                                    hotelStayAdapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(HotelStayViewAllActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(HotelStayViewAllActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<HotelStayResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(HotelStayViewAllActivity.this);
        }
    }
}