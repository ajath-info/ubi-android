package com.ubi.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.AdPackageActivity;
import com.ubi.android.MainActivity;
import com.ubi.android.R;
import com.ubi.android.adapters.ProductDetailImageAdapter;
import com.ubi.android.models.PreviewAdData;
import com.ubi.android.models.PreviewAdReponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewAdActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView title, pendingtv, pricetv, expiry, desctv, applybtn, pagecount;
    Button sellbtn;
    ProgressDialog dialog;
    LinearLayout applybtnlay;
    ViewPager viewpager;
    ArrayList<String> images = new ArrayList<>();
    ProductDetailImageAdapter imageAdapter;
    int totalcount;
    String ads_id;
    boolean isdetailonly;
    TextView locationtv;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_ad);
        applybtnlay = findViewById(R.id.applybtnlay);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isdetailonly)
                    finish();
                else {
                    Intent intent = new Intent(PreviewAdActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        pendingtv = findViewById(R.id.pendingtv);
        locationtv = findViewById(R.id.locationtv);
        isdetailonly = getIntent().getBooleanExtra("isdetailonly", false);
        dialog = new ProgressDialog(PreviewAdActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        pagecount = findViewById(R.id.pagecount);
        title = findViewById(R.id.title);
        pricetv = findViewById(R.id.pricetv);
        expiry = findViewById(R.id.expiry);
        desctv = findViewById(R.id.desctv);
        applybtn = findViewById(R.id.applybtn);
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdPackageActivity.class);
                intent.putExtra("ads_id", ads_id);
                startActivity(intent);
                finish();
            }
        });

        viewpager = findViewById(R.id.viewpager);
        imageAdapter = new ProductDetailImageAdapter(this, images);
        viewpager.setAdapter(imageAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagecount.setText((position + 1) + "/" + totalcount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ads_id = getIntent().getStringExtra("ads_id");

        sellbtn = findViewById(R.id.sellbtn);
        sellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviewAdActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        if (isdetailonly) {
            applybtn.setVisibility(View.GONE);
            sellbtn.setVisibility(View.GONE);
            pendingtv.setVisibility(View.GONE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void previewAd(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            dialog.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<PreviewAdReponse> call = apiInterface.previewAds(token, params);
            call.enqueue(new Callback<PreviewAdReponse>() {
                @Override
                public void onResponse(Call<PreviewAdReponse> call, Response<PreviewAdReponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        dialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        PreviewAdData data = response.body().payload;
                                        images.addAll(response.body().payload.getImage());
                                        totalcount = images.size();
                                        pagecount.setText("1/" + totalcount);
                                        imageAdapter.notifyDataSetChanged();
                                        title.setText(data.getName());
                                        pricetv.setText("$" + data.getPrice());
                                        expiry.setText("Ad expire on " + data.getEnd_date());
                                        desctv.setText(Html.fromHtml(data.getDescription()));
                                        locationtv.setText(data.getLocation());
                                        if (data.is_payment.equalsIgnoreCase("false")) {
                                            applybtn.setVisibility(View.VISIBLE);
                                            applybtnlay.setVisibility(View.VISIBLE);
                                        } else {
                                            applybtn.setVisibility(View.GONE);
                                            applybtnlay.setVisibility(View.GONE);
                                        }
                                        String loc = data.getLocation();
                                        if (!TextUtils.isEmpty(loc))
                                            getLocationFromAddress(loc);
                                    }
                                }
                            }
                        } else {
                            AppUtils.showalert(PreviewAdActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PreviewAdReponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    dialog.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(PreviewAdActivity.this);
        }
    }

    public void getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                location.getLongitude();
//        ((double) (location.getLatitude() * 1E6),
//                    (double) (location.getLongitude() * 1E6));
                Log.d(PreviewAdActivity.class.getName(), "Lat " + location.getLatitude() + " Lng " + location.getLongitude());
                Log.d(PreviewAdActivity.class.getName(), "1E6 Lat " + (location.getLatitude() * 1E6) + " Lng " + (location.getLongitude() * 1E6));
                LatLng TutorialsPoint = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new
                        MarkerOptions().position(TutorialsPoint).title(title.getText().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(TutorialsPoint);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            } else {
                AppUtils.showalert(PreviewAdActivity.this, "Unable to find address from GCP", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ads_id", ads_id);
                previewAd(params);

            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isdetailonly)
            finish();
        else {
            Intent intent = new Intent(PreviewAdActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}