package com.ubi.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.PostAdResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostAdFourthStepActivity extends AppCompatActivity {
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_fourth_step);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pd = new ProgressDialog(PostAdFourthStepActivity.this);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        EditText nametv = findViewById(R.id.nametv);
        EditText emailtv = findViewById(R.id.emailtv);
        EditText mobile = findViewById(R.id.mobile);
        EditText company = findViewById(R.id.company);
        UserData data = AppPreferences.getInstance().getUserData(getApplicationContext());
        if (data != null) {
            nametv.setText(data.getFirst_name());
            emailtv.setText(data.getEmail());
            mobile.setText(data.getPhone());
        }
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strnametv = nametv.getText().toString();
                String stremailtv = emailtv.getText().toString();
                String strmobile = mobile.getText().toString();
                String strcompany = company.getText().toString();
                if (TextUtils.isEmpty(strnametv)) {
                    AppUtils.showalert(PostAdFourthStepActivity.this, "Please enter name", false);
                } else if (TextUtils.isEmpty(stremailtv)) {
                    AppUtils.showalert(PostAdFourthStepActivity.this, "Please enter email", false);
                } else if (TextUtils.isEmpty(strmobile)) {
                    AppUtils.showalert(PostAdFourthStepActivity.this, "Please enter mobile", false);
                } else if (TextUtils.isEmpty(strcompany)) {
                    AppUtils.showalert(PostAdFourthStepActivity.this, "Please enter company", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("category_id", getIntent().getStringExtra("category_id"));
                    params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
                    params.put("name", getIntent().getStringExtra("title"));
                    params.put("description", getIntent().getStringExtra("desc"));
                    params.put("image", getIntent().getStringExtra("uploadpath"));
                    params.put("price", getIntent().getStringExtra("price"));
                    params.put("location", getIntent().getStringExtra("location"));
                    params.put("state", getIntent().getStringExtra("sub_category_id"));
                    params.put("counrty", getIntent().getStringExtra("sub_category_id"));
                    submitAd(params);
                }
            }
        });
    }

    private void submitAd(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<PostAdResponse> call = apiInterface.postAds(token, params);
            call.enqueue(new Callback<PostAdResponse>() {
                @Override
                public void onResponse(Call<PostAdResponse> call, Response<PostAdResponse> response) {
                    Log.d(PostAdFourthStepActivity.class.getName(), response.toString());
                    try {
                        if (pd != null)
                            pd.dismiss();
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PostAdFourthStepActivity.this, PostAdSuccessActivity.class);
                            intent.putExtra("issuccess", true);
                            intent.putExtra("code", response.body().code);
                            intent.putExtra("product_id", response.body().payload.product_id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(PostAdFourthStepActivity.this, PostAdSuccessActivity.class);
                            intent.putExtra("issuccess", false);
                            intent.putExtra("product_id", "");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
//                            AppUtils.showalert(PostAdFourthStepActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PostAdResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(PostAdFourthStepActivity.this);
        }
    }
}