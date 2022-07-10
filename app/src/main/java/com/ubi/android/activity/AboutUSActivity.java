package com.ubi.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.AboutResponse;
import com.ubi.android.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutUSActivity extends AppCompatActivity {

    TextView aboutus, aboutustitle, version;
    LinearLayout backlay;
    ProgressBar progressbar;
    ImageView img;
    String app_url_ios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_usactivity);
        img = findViewById(R.id.img);
        version = findViewById(R.id.version);
        aboutus = findViewById(R.id.aboutus);
        backlay = findViewById(R.id.backlay);
        aboutustitle = findViewById(R.id.aboutustitle);
        progressbar = findViewById(R.id.progressbar);
        getAboutData();
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.androidlink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        findViewById(R.id.ioslink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!TextUtils.isEmpty(app_url_ios)) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(app_url_ios));
                        startActivity(i);
                    }
                } catch (Exception e) {
                    AppUtils.showalert(AboutUSActivity.this, e.getMessage(), false);
                }
            }
        });
    }

    private void getAboutData() {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<AboutResponse> call = apiInterface.aboutInfo();
            call.enqueue(new Callback<AboutResponse>() {
                @Override
                public void onResponse(Call<AboutResponse> call, Response<AboutResponse> response) {
                    Log.d("TAG", response.code() + "");
                    progressbar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            aboutus.setText(response.body().payload.description);
                            aboutustitle.setText(response.body().payload.title);
                            if (!TextUtils.isEmpty(response.body().payload.logo)) {
                                Picasso.get().load(response.body().payload.logo).into(img);
                            }
                            version.setText("Version\n" + response.body().payload.app_version_android);
                            app_url_ios = response.body().payload.app_url_ios;
                        } else {
                            AppUtils.showalert(AboutUSActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(AboutUSActivity.this, response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<AboutResponse> call, Throwable t) {
                    call.cancel();
                    AppUtils.showalert(AboutUSActivity.this, t.getMessage(), false);
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(AboutUSActivity.this);
        }
    }
}