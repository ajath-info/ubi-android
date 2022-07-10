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

import com.chaos.view.PinView;
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

public class ForgotPasswordVerifyActivity extends BasicActivity {

    TextView resenttv;
    PinView otpview;
    Button verifyotp;
    String email;
    LinearLayout backlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
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
        init();
    }

    private void init() {
        resenttv = findViewById(R.id.resenttv);
        otpview = findViewById(R.id.firstPinView);
        verifyotp = findViewById(R.id.verifyotp);
        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otpview.getText().toString();
                if (TextUtils.isEmpty(otp)) {
                    AppUtils.showalert(ForgotPasswordVerifyActivity.this, "Please enter OTP", false);

                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("user_type", "1");
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
                params.put("email", email);
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
        AppUtils.hidekeyboard(ForgotPasswordVerifyActivity.this, otpview);
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.forgotPasswordOtpVerified(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordVerifyActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showalert(ForgotPasswordVerifyActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(ForgotPasswordVerifyActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(ForgotPasswordVerifyActivity.this);
        }
    }

    private void resend(Map<String, String> params) {
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
                        } else {
                            AppUtils.showalert(ForgotPasswordVerifyActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(ForgotPasswordVerifyActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(ForgotPasswordVerifyActivity.this);
        }
    }
}