package com.ubi.android.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.ContactUsResponse;
import com.ubi.android.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUSActivity extends AppCompatActivity {

    TextView emailtv, mobiletv, websitetv, whatsappmobiletv;
    LinearLayout backlay;
    ProgressBar progressbar;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus_activity);
        img = findViewById(R.id.img);
        websitetv = findViewById(R.id.websitetv);
        emailtv = findViewById(R.id.emailtv);
        backlay = findViewById(R.id.backlay);
        mobiletv = findViewById(R.id.mobiletv);
        progressbar = findViewById(R.id.progressbar);
        whatsappmobiletv = findViewById(R.id.whatsappmobiletv);
        getAboutData();
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.mobiletv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ContactUSActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                                100);
                    } else
                        calluser(mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.emailtv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeEmail();
            }
        });

        findViewById(R.id.websitetv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = website;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {

                }

            }
        });

        findViewById(R.id.whatsappmobiletv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsAppConversation(mobile, "Hi");
            }
        });
    }

    public void openWhatsAppConversation(String number, String message) {
        try {
            number = number.replace(" ", "").replace("+", "");
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    String mobile, email, website;

    private void calluser(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hi Beezy");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void getAboutData() {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<ContactUsResponse> call = apiInterface.contact();
            call.enqueue(new Callback<ContactUsResponse>() {
                @Override
                public void onResponse(Call<ContactUsResponse> call, Response<ContactUsResponse> response) {
                    Log.d("TAG", response.code() + "");
                    progressbar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            emailtv.setText(response.body().payload.email);
                            mobiletv.setText(response.body().payload.phone);
                            websitetv.setText(response.body().payload.website);
                            if (!TextUtils.isEmpty(response.body().payload.logo)) {
                                Picasso.get().load(response.body().payload.logo).into(img);
                            }
                            mobile = response.body().payload.phone;
                            email = response.body().payload.email;
                            website = response.body().payload.website;
                            whatsappmobiletv.setText(response.body().payload.phone);

                        } else {
                            AppUtils.showalert(ContactUSActivity.this, response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(ContactUSActivity.this, response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<ContactUsResponse> call, Throwable t) {
                    call.cancel();
                    AppUtils.showalert(ContactUSActivity.this, t.getMessage(), false);
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ContactUSActivity.this);
        }
    }
}