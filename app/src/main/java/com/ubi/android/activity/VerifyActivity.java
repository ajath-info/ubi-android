package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
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

public class VerifyActivity extends BasicActivity {

    TextView resenttv;
    PinView otpview;
    Button verifyotp;
    String phone;
    TextView otptext;
    LinearLayout backlay;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        phone = getIntent().getStringExtra("phone");
        init();
        getToken();
    }

    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        token = task.getResult();
                        Log.d(LoginActivity.class.getName(), token);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        otptext = findViewById(R.id.otptext);
        otptext.setText("Enter the OTP sent to\n" + phone);
        resenttv = findViewById(R.id.resenttv);
        otpview = findViewById(R.id.firstPinView);
        verifyotp = findViewById(R.id.verifyotp);
        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpview.getText().toString();
                if (TextUtils.isEmpty(otp)) {
                    AppUtils.showalert(VerifyActivity.this, "Please enter OTP", false);

                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("phone", phone);
                    params.put("user_type", "1");
                    params.put("device_type", "A");
                    params.put("device_token", token);
                    params.put("otp", otp);
                    verifyotp(params);
                }
            }
        });
        setupregisterview();
    }

    private void setupregisterview() {
        String str1 = "Don't receive the OTP? ";
        String str2 = "RESEND OTP";
        SpannableString forgottext = new SpannableString(str1 + str2);
        ClickableSpan forgotclickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("user_type", "1");
                resend(params);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.registertext));
            }
        };
        forgottext.setSpan(forgotclickableSpan, str1.length(), forgottext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        resenttv.setText(forgottext);
        resenttv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void verifyotp(Map<String, String> params) {
        AppUtils.hidekeyboard(VerifyActivity.this, otpview);
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.verifyOtpLogin(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            AppPreferences.getInstance().setUserLoggedIn(getApplicationContext(), true);
                            AppPreferences.getInstance().setToken(getApplicationContext(), response.body().payload.getAuth_token());
                            AppPreferences.getInstance().setUserData(getApplicationContext(), response.body().payload);
                            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showalert(VerifyActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(VerifyActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(VerifyActivity.this);
        }
    }

    private void resend(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.reSendOtp(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_LONG).show();
                        } else {
                            AppUtils.showalert(VerifyActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(VerifyActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(VerifyActivity.this);
        }
    }
}