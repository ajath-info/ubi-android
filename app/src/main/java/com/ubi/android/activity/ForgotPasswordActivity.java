package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.UserResponse;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BasicActivity {

    EditText emailtv;
    Button submitbtn;
    LinearLayout backlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        backlay = findViewById(R.id.backlay);
        if (backlay != null) {
            backlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        emailtv = findViewById(R.id.emailtv);
        submitbtn = findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = emailtv.getText().toString();
                if (TextUtils.isEmpty(mail)) {
                    AppUtils.showalert(ForgotPasswordActivity.this, "Please enter email", false);
                } else if (!AppUtils.isEditTextContainEmail(emailtv)) {
                    AppUtils.showalert(ForgotPasswordActivity.this, "Please enter valid email", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", mail);
                    params.put("user_type", "1");
                    forgot(params, mail);
                }
            }
        });
    }

    private void forgot(Map<String, String> params, String mail) {
        AppUtils.hidekeyboard(ForgotPasswordActivity.this, emailtv);
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.forgotPasswordEmail(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordVerifyActivity.class);
                            intent.putExtra("email", mail);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showalert(ForgotPasswordActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(ForgotPasswordActivity.this, response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    call.cancel();
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(ForgotPasswordActivity.this);
        }
    }
}