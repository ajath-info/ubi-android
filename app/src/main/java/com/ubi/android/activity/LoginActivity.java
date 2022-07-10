package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class LoginActivity extends BasicActivity {

    TextView registertv, emailtv, phonenumbertv;
    RelativeLayout emaillay, phonelay;
    LinearLayout phonemainlay, passwordmainlay, emailmainlay;
    EditText emailetv, passwordetv, phoneetv;
    int type = 1;
    String phone;
    boolean showpassword = false;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initviews();
        getToken();
    }

    private void getToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        token = task.getResult();
                        Log.d(LoginActivity.class.getName(),token);
                    }else{
                        Log.w(LoginActivity.class.getName(), "Fetching FCM registration token failed", task.getException());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initviews() {
        emailetv = findViewById(R.id.emailetv);
        passwordetv = findViewById(R.id.passwordetv);
        phoneetv = findViewById(R.id.phoneetv);
        phonemainlay = findViewById(R.id.phonemainlay);
        passwordmainlay = findViewById(R.id.passwordmainlay);
        emailmainlay = findViewById(R.id.emailmainlay);
        emailtv = findViewById(R.id.emailtv);
        phonenumbertv = findViewById(R.id.phonenumbertv);
        registertv = findViewById(R.id.registertv);
        findViewById(R.id.forgotpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        emaillay = findViewById(R.id.emaillay);
        phonelay = findViewById(R.id.phonelay);
        emaillay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                phonemainlay.setVisibility(View.GONE);
                passwordmainlay.setVisibility(View.VISIBLE);
                emailmainlay.setVisibility(View.VISIBLE);
                emaillay.setBackgroundResource(R.drawable.logintabbtnbg);
                phonelay.setBackgroundResource(R.drawable.loginbtnheaderbg);
                phonenumbertv.setTextColor(getResources().getColor(R.color.black));
                emailtv.setTextColor(getResources().getColor(R.color.white));
                emaillay.setElevation(2);
                phonelay.setElevation(0);
            }
        });

        phonelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 2;
                phonemainlay.setVisibility(View.VISIBLE);
                passwordmainlay.setVisibility(View.GONE);
                emailmainlay.setVisibility(View.GONE);
                emaillay.setBackgroundResource(R.drawable.loginbtnheaderbg);
                phonelay.setBackgroundResource(R.drawable.logintabbtnbg);
                phonenumbertv.setTextColor(getResources().getColor(R.color.white));
                emailtv.setTextColor(getResources().getColor(R.color.black));
                emaillay.setElevation(0);
                phonelay.setElevation(2);
            }
        });
        setupregisterview();
        findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailetv.getText().toString();
                String password = passwordetv.getText().toString();
                phone = phoneetv.getText().toString();
                if (type == 1 && TextUtils.isEmpty(email)) {
                    AppUtils.showalert(LoginActivity.this, "Please enter email", false);
                } else if (type == 1 && !AppUtils.isEditTextContainEmail(emailetv)) {
                    AppUtils.showalert(LoginActivity.this, "Please enter valid email", false);
                } else if (type == 1 && TextUtils.isEmpty(password)) {
                    AppUtils.showalert(LoginActivity.this, "Please enter password", false);
                } else if (type == 2 && TextUtils.isEmpty(phone)) {
                    AppUtils.showalert(LoginActivity.this, "Please enter mobile number", false);
                } else if (type == 2 && phone.length() < 10) {
                    AppUtils.showalert(LoginActivity.this, "Please enter valid mobile number", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", type == 1 ? email : "");
                    params.put("password", type == 1 ? password : "");
                    params.put("phone", type == 2 ? phone : "");
                    params.put("user_type", "1");
                    params.put("device_token", token);
                    params.put("device_type", "A");
                    params.put("login_type", type == 1 ? "E" : "P");
                    Login(params);
                }
            }
        });

        findViewById(R.id.showpassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!showpassword) {
                    passwordetv.setTransformationMethod(null);
                    showpassword = true;
                } else {
                    showpassword = false;
                    passwordetv.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }

    private void setupregisterview() {
        String str1 = "Don't have an account? ";
        String str2 = "Register";
        SpannableString forgottext = new SpannableString(str1 + str2);
        ClickableSpan forgotclickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.registertext));
            }
        };
        forgottext.setSpan(forgotclickableSpan, str1.length(), forgottext.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registertv.setText(forgottext);
        registertv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void Login(Map<String, String> params) {
        AppUtils.hidekeyboard(LoginActivity.this, emailetv);
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<UserResponse> call = apiInterface.loginUser(params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                            if (type == 1) {
                                AppPreferences.getInstance().setUserLoggedIn(getApplicationContext(), true);
                                AppPreferences.getInstance().setToken(getApplicationContext(), response.body().payload.getAuth_token());
                                AppPreferences.getInstance().setUserData(getApplicationContext(), response.body().payload);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else if (type == 2) {
                                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                                intent.putExtra("phone", phone);
                                startActivity(intent);

                            }
                        } else {
                            AppUtils.showalert(LoginActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(LoginActivity.this, response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    call.cancel();
                    AppUtils.showalert(LoginActivity.this, t.getMessage(), false);
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(LoginActivity.this);
        }
    }
}