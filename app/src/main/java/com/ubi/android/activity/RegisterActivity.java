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

import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.MainActivity;
import com.ubi.android.R;
import com.ubi.android.models.UserResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BasicActivity {
    EditText firstnametv, lastnametv, mobiletv, emailtv, citytv, passwordtv, confirmpasswordtv;
    Button submitbtntv;
    LinearLayout backlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        backlay = findViewById(R.id.backlay);
        if (backlay != null) {
            backlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        firstnametv = findViewById(R.id.firstnametv);
        lastnametv = findViewById(R.id.lastnametv);
        mobiletv = findViewById(R.id.mobiletv);
        emailtv = findViewById(R.id.emailtv);
        citytv = findViewById(R.id.citytv);
        passwordtv = findViewById(R.id.passwordtv);
        confirmpasswordtv = findViewById(R.id.confirmpasswordtv);
        submitbtntv = findViewById(R.id.submitbtntv);
        submitbtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstnametv.getText().toString();
                String lastname = lastnametv.getText().toString();
                String mobile = mobiletv.getText().toString();
                String email = emailtv.getText().toString();
                String city = citytv.getText().toString();
                String password = passwordtv.getText().toString();
                String confirmpassword = confirmpasswordtv.getText().toString();
                if (TextUtils.isEmpty(firstname)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter first name", false);
                } else if (TextUtils.isEmpty(lastname)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter last name", false);
                } else if (TextUtils.isEmpty(mobile)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter mobile number", false);
                }
//                else if (mobile.length() < 9) {
//                    AppUtils.showalert(RegisterActivity.this, "Please enter valid mobile number", false);
//                }
                else if (TextUtils.isEmpty(email)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter email", false);
                } else if (!AppUtils.isEditTextContainEmail(emailtv)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter valid email", false);
                } else if (TextUtils.isEmpty(city)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter city", false);
                } else if (TextUtils.isEmpty(password)) {
                    AppUtils.showalert(RegisterActivity.this, "Please enter password", false);
                } else if (TextUtils.isEmpty(confirmpassword)) {
                    AppUtils.showalert(RegisterActivity.this, "Please confirm password", false);
                } else if (!password.equalsIgnoreCase(confirmpassword)) {
                    AppUtils.showalert(RegisterActivity.this, "Password not matched", false);
                    confirmpasswordtv.setText("");
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name", firstname);
                    params.put("last_name", lastname);
                    params.put("phone", mobile);
                    params.put("city", city);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("user_type", "1");
                    params.put("device_token", "BTC3UHfjR/hIv1IEALkXQ+wBZOVn33");
                    params.put("device_type", "A");
                    params.put("login_type", "E");
                    register(params, mobile);
                }
            }
        });
    }

    private void register(Map<String, String> params, String phone) {
        AppUtils.hidekeyboard(RegisterActivity.this, emailtv);
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.registerUser(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            AppPreferences.getInstance().setUserLoggedIn(getApplicationContext(),true);
                            AppPreferences.getInstance().setToken(getApplicationContext(),response.body().payload.getAuth_token());
                            AppPreferences.getInstance().setUserData(getApplicationContext(), response.body().payload);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {

                            AppUtils.showalert(RegisterActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(RegisterActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(RegisterActivity.this);
        }
    }
}