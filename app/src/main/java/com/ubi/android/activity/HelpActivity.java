package com.ubi.android.activity;

import android.app.ProgressDialog;
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
import com.ubi.android.models.BaseResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends AppCompatActivity {
    EditText desctv, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        desctv = findViewById(R.id.desctv);
        subject = findViewById(R.id.subject);
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strsubject = subject.getText().toString();
                String comment = desctv.getText().toString();
                if (TextUtils.isEmpty(strsubject)) {
                    AppUtils.showalert(HelpActivity.this, "Please enter your subject", false);
                } else if (TextUtils.isEmpty(comment)) {
                    AppUtils.showalert(HelpActivity.this, "Please enter your comment", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("subject", strsubject);
                    params.put("message", comment);
                    submitRating(params);
                }
            }
        });
    }

    private void submitRating(Map<String, String> params) {
        ProgressDialog dialog = new ProgressDialog(HelpActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please wait...");
        dialog.show();

        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getApplicationContext());
            Call<BaseResponse> call = apiInterface.help(token, params);
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
                            } else if (response.body().code == 3) {
                                Toast.makeText(HelpActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                AppUtils.clearcache(HelpActivity.this);
                            } else {
                                AppUtils.showalert(HelpActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(HelpActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(HelpActivity.this);
        }
    }
}