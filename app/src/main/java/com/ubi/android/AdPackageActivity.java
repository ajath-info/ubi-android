package com.ubi.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.activity.PaymentActivity;
import com.ubi.android.adapters.AdPackageAdapter;
import com.ubi.android.adapters.ProductDetailImageAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.PackageModel;
import com.ubi.android.models.PackageResponse;
import com.ubi.android.models.PreviewAdReponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdPackageActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<PackageModel> locations = new ArrayList<>();
    AdPackageAdapter adapter;
    TextView nodata;
    ViewPager viewpager;
    ArrayList<String> images = new ArrayList<>();
    ProductDetailImageAdapter imageAdapter;
    TextView pagecount;
    int totalcount = 0;
    ProgressDialog dialog;
    TextView applybtn;
    String type, price, package_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_package);
        applybtn = findViewById(R.id.applybtn);
        dialog = new ProgressDialog(AdPackageActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(type)) {
                    Intent intent = new Intent(AdPackageActivity.this, PaymentActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("price", price);
                    intent.putExtra("ads_id", getIntent().getStringExtra("ads_id"));
                    intent.putExtra("package_id", package_id);
                    startActivity(intent);
                    finish();
                } else {
                    AppUtils.showalert(AdPackageActivity.this, "Please select package", false);
                }
            }
        });

    }

    private void init() {
        pagecount = findViewById(R.id.pagecount);
        nodata = findViewById(R.id.nodata);
        adapter = new AdPackageAdapter(this, locations);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                for (int i = 0; i < locations.size(); i++) {
                    locations.get(i).isChecked = false;
                }
                locations.get(pos).isChecked = true;
                adapter.notifyDataSetChanged();
                applybtn.setText("Pay $" + locations.get(pos).price);
                type = locations.get(pos).title;
                price = locations.get(pos).price;
                package_id = locations.get(pos).id;
            }
        });

        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(adapter);
        getProductDetails();
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
        String ads_id = getIntent().getStringExtra("ads_id");
        Map<String, String> params = new HashMap<String, String>();
        params.put("ads_id", ads_id);
        previewAd(params);
    }

    private void getProductDetails() {
        if (AppUtils.isConnectingToInternet(this)) {
            locations.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<PackageResponse> call = apiInterface.packages();
            call.enqueue(new Callback<PackageResponse>() {
                @Override
                public void onResponse(Call<PackageResponse> call, Response<PackageResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        locations.addAll(response.body().payload);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                nodata.setText(response.body().message);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        } else {
                            AppUtils.showalert(AdPackageActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PackageResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(AdPackageActivity.this);
        }
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
                                        images.addAll(response.body().payload.getImage());
                                        totalcount = images.size();
                                        pagecount.setText("1/" + totalcount);
                                        imageAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else {
                            AppUtils.showalert(AdPackageActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(AdPackageActivity.this);
        }
    }
}