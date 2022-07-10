package com.ubi.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class ResetPasswordActivity extends BasicActivity {

    String email;
    EditText passwordtv, confirmpasswordtv;
    Button submitbtn;
    LinearLayout backlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        backlay = findViewById(R.id.backlay);
        if (backlay != null) {
            backlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        email = getIntent().getStringExtra("email");
        passwordtv = findViewById(R.id.passwordtv);
        confirmpasswordtv = findViewById(R.id.confirmpasswordtv);
        submitbtn = findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordtv.getText().toString();
                String cnfpassword = confirmpasswordtv.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    AppUtils.showalert(ResetPasswordActivity.this, "Please enter password", false);
                } else if (password.length() < 6) {
                    AppUtils.showalert(ResetPasswordActivity.this, "Password strength is low, length must be greater than six", false);
                } else if (TextUtils.isEmpty(cnfpassword)) {
                    AppUtils.showalert(ResetPasswordActivity.this, "Please confirm password", false);
                } else if (!password.equalsIgnoreCase(cnfpassword)) {
                    AppUtils.showalert(ResetPasswordActivity.this, "Password not matched", false);
                    confirmpasswordtv.setText("");
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("user_type", "1");
                    params.put("password", password);
                    params.put("confirm_password", cnfpassword);
                    resetpassword(params);
                }
            }
        });
    }

    private void resetpassword(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.resetPasswordEmail(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            AppUtils.showalert(ResetPasswordActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(ResetPasswordActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(ResetPasswordActivity.this);
        }
    }
}