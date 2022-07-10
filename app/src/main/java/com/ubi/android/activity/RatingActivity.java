package com.ubi.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends AppCompatActivity {

    AppCompatRatingBar rating;
    EditText desctv;
    boolean isseller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        isseller = getIntent().getBooleanExtra("isseller", false);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        desctv = findViewById(R.id.desctv);
        rating = findViewById(R.id.rating);
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rat = rating.getRating();
                String comment = desctv.getText().toString();
                if (rat == 0) {
                    AppUtils.showalert(RatingActivity.this, "Please give atleast one rating", false);
                } else if (TextUtils.isEmpty(comment)) {
                    AppUtils.showalert(RatingActivity.this, "Please enter your comment", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    if (isseller)
                        params.put("vendor_id", getIntent().getStringExtra("vendor_id"));
                    else {
                        params.put("product_id", getIntent().getStringExtra("product_id"));
                        params.put("category_id", getIntent().getStringExtra("category_id"));
                        params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
                    }
                    params.put("rating", ""+rat);
                    params.put("review", comment);
                    submitRating(params);
                }
            }
        });
    }

    private void submitRating(Map<String, String> params) {
        ProgressDialog dialog = new ProgressDialog(RatingActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait...");
        dialog.show();

        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getApplicationContext());
            Call<BaseResponse> call = apiInterface.rateProduct(token, params);
            if (isseller) {
                call = apiInterface.rateVendor(token, params);
            }
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        dialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                AppUtils.showalert(RatingActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(RatingActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    call.cancel();
                    t.printStackTrace();
                    dialog.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(RatingActivity.this);
        }
    }
}