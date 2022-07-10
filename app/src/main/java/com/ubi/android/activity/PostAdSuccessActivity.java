package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.AdPackageActivity;
import com.ubi.android.MainActivity;
import com.ubi.android.R;

import java.util.HashMap;
import java.util.Map;

public class PostAdSuccessActivity extends AppCompatActivity {

    ImageView image;
    TextView sellbtn, applybtn;
    String ads_id = "";
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_success);
        code = getIntent().getIntExtra("code", 0);
        image = findViewById(R.id.image);
        applybtn = findViewById(R.id.applybtn);
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PreviewAdActivity.class);
                intent.putExtra("ads_id", ads_id);
                intent.putExtra("isdetailonly", false);
                startActivity(intent);
                finish();
            }
        });
        sellbtn = findViewById(R.id.sellbtn);
        if (!getIntent().getBooleanExtra("issuccess", true)) {
            image.setImageResource(R.drawable.error);
            sellbtn.setVisibility(View.GONE);
            applybtn.setVisibility(View.GONE);
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("page", "1");
            params.put("limit", "1");
//            myads(params);
        }
        sellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdPackageActivity.class);
                intent.putExtra("ads_id", ads_id);
                startActivity(intent);
                finish();
            }
        });
        if (code == 1)
            sellbtn.setVisibility(View.GONE);
        ads_id = getIntent().getStringExtra("product_id");
    }

//    private void myads(Map<String, String> params) {
//        if (AppUtils.isConnectingToInternet(this)) {
//            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
//            String token = AppPreferences.getInstance().getToken(this);
//            Call<MyAdsResponse> call = apiInterface.myAds(token, params);
//            call.enqueue(new Callback<MyAdsResponse>() {
//                @Override
//                public void onResponse(Call<MyAdsResponse> call, Response<MyAdsResponse> response) {
//                    Log.d("TAG", response.code() + "");
//                    try {
//                        if (response.code() == 200) {
//                            if (response.body().code == 1) {
//                                if (response.body().payload != null) {
//                                    if (response.body().payload != null && response.body().payload.size() > 0) {
//                                        MyAds myad = response.body().payload.get(0);
//                                        ads_id = myad.getAds_id();
//                                    }
//                                }
//                            }
//                        } else {
//                            AppUtils.showalert(PostAdSuccessActivity.this, response.message(), false);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<MyAdsResponse> call, Throwable t) {
//                    t.printStackTrace();
//                    call.cancel();
//                }
//            });
//        } else {
//            AppUtils.Nointernetalert(PostAdSuccessActivity.this);
//        }
//    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostAdSuccessActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}