package com.ubi.android.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chaek.android.RatingBar;
import com.make.dots.dotsindicator.DotsIndicator;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.BuildConfig;
import com.ubi.android.R;
import com.ubi.android.adapters.ProductDetailImageAdapter;
import com.ubi.android.adapters.RecommendProductAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.ProductDetailModel;
import com.ubi.android.models.ProductDetailResponse;
import com.ubi.android.models.RecommendedProducts;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    ProductDetailImageAdapter imageAdapter;
    ViewPager viewpager;
    ArrayList<String> images = new ArrayList<>();
    DotsIndicator dotsIndicator;
    ProgressBar progressbar;
    TextView lastseentv, pricetv, desc, featuretv, sellertitle, selleraddress, sellerratingtv, sellertotalrating;
    RatingBar sellerrating;
    ImageView venorimg;
    RecyclerView recyclerview;
    RecommendProductAdapter adapter;
    ArrayList<RecommendedProducts> recommendedProducts = new ArrayList<>();
    RelativeLayout imageslay;
    UserData userData;
    ImageView hearticon;
    TextView locationtv;
    ImageView whatsappicon, callappicon;
    LinearLayout locationlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        userData = AppPreferences.getInstance().getUserData(getApplicationContext());
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    boolean iscollasped = false;

    private void init() {
        locationlay = findViewById(R.id.locationlay);
        callappicon = findViewById(R.id.callappicon);
        whatsappicon = findViewById(R.id.whatsappicon);
        locationtv = findViewById(R.id.locationtv);
        ImageView arrowimg = findViewById(R.id.arrowimg);
        hearticon = findViewById(R.id.hearticon);
        viewmorelay = findViewById(R.id.viewmorelay);
        viewmorelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!iscollasped) {
                    iscollasped = true;
                    featuretv.setMaxLines(1000);
                    if (data != null)
                        featuretv.setText(Html.fromHtml(data.getFeatures()));
                    arrowimg.setRotation(180);
                } else {
                    arrowimg.setRotation(0);
                    iscollasped = false;
                    featuretv.setMaxLines(3);
                    if (data != null)
                        featuretv.setText(Html.fromHtml(data.getFeatures()));
                }
            }
        });
        findViewById(R.id.companyvideolay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data != null) {
                    Intent intent = new Intent(getApplicationContext(), PlayVideoActivity.class);
                    intent.putExtra("videourl", data.getAbout_the_seller().getCompany_video());
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        imageslay = findViewById(R.id.imageslay);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        lastseentv = findViewById(R.id.lastseentv);
        venorimg = findViewById(R.id.venorimg);
        sellerrating = findViewById(R.id.sellerrating);
        sellerrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RatingActivity.class);
                intent.putExtra("isseller", true);
                intent.putExtra("vendor_id", data.getVendor_id());
                startActivity(intent);
            }
        });
        pricetv = findViewById(R.id.pricetv);
        desc = findViewById(R.id.desc);
        featuretv = findViewById(R.id.featuretv);
        sellertitle = findViewById(R.id.sellertitle);
        selleraddress = findViewById(R.id.selleraddress);
        sellerratingtv = findViewById(R.id.sellerratingtv);
        sellertotalrating = findViewById(R.id.sellertotalrating);
        progressbar = findViewById(R.id.progressbar);
        viewpager = findViewById(R.id.viewpager);
        imageAdapter = new ProductDetailImageAdapter(this, images);
        viewpager.setAdapter(imageAdapter);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        Map<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));
        getProductDetails(params);
        adapter = new RecommendProductAdapter(this, recommendedProducts);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.hearticon:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("product_id", recommendedProducts.get(pos).getId());
                        favunfav(params, recommendedProducts.get(pos).getIs_fav(), pos);
                        break;
                    case R.id.mainlay:
                        Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", recommendedProducts.get(pos).getId());
                        startActivity(intent);
                        break;
                }
            }
        });
        recyclerview.setAdapter(adapter);
    }

    private void getProductDetails(Map<String, String> params) {
        Log.d(ProductDetailActivity.class.getName(), params.toString());
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<ProductDetailResponse> call = apiInterface.productDetails(token, params);
            call.enqueue(new Callback<ProductDetailResponse>() {
                @Override
                public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                    Log.d(ProductDetailActivity.class.getName(), response.toString());
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    setData(response.body().payload);
                                }
                            } else if (response.body().code == 3) {
                                Toast.makeText(ProductDetailActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                AppUtils.clearcache(ProductDetailActivity.this);
                            } else {
                                AppUtils.showalert(ProductDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ProductDetailActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                    call.cancel();
                    t.printStackTrace();
                    progressbar.setVisibility(View.GONE);
                    AppUtils.showalert(ProductDetailActivity.this, t.getMessage(), true);
                }
            });
        } else {
            AppUtils.Nointernetalert(ProductDetailActivity.this);
        }
    }

    ProductDetailModel data;
    LinearLayout viewmorelay;

    private void setData(ProductDetailModel data) {
        this.data = data;
        locationtv.setText(data.getLocation());
        images.addAll(data.getImage());
        imageAdapter.notifyDataSetChanged();
        pricetv.setText("$" + data.getPrice());
        desc.setText(Html.fromHtml(data.getDescription()));
        featuretv.setText(Html.fromHtml(data.getFeatures()));
        sellertitle.setText(Html.fromHtml(data.getAbout_the_seller().getCompany_name()));
        selleraddress.setText(Html.fromHtml(data.getAbout_the_seller().getLocation()));
        sellerratingtv.setText(data.getAbout_the_seller().getAvg_seller_rating() + "/" + data.getAbout_the_seller().getTotla_seller_rating());
        sellertotalrating.setText("(" + data.getAbout_the_seller().getTotla_seller_rating() + ")");
        if (TextUtils.isEmpty(data.getAbout_the_seller().getAvg_seller_rating()))
            sellerrating.setScore(Integer.parseInt(data.getAbout_the_seller().getAvg_seller_rating()));
        lastseentv.setText("Last seen " + data.getAbout_the_seller().getLast_seen());

        recommendedProducts.addAll(data.getRecommended_products());
        adapter.notifyDataSetChanged();
        dotsIndicator.setViewPager(viewpager);
        RequestOptions myOptions = new RequestOptions()
                .override(300, 300).centerCrop();
        if (!TextUtils.isEmpty(data.getAbout_the_seller().profile_img))
            Glide.with(this).load(data.getAbout_the_seller().profile_img).apply(myOptions).into(venorimg);
//            Picasso.get().load(data.getAbout_the_seller().profile_img).rotate(90).resize(300, 300).centerCrop().into(venorimg);

        if (data.getImage().size() > 0) {
            imageslay.setVisibility(View.VISIBLE);
        } else {
            imageslay.setVisibility(View.GONE);
        }

        LinearLayout bestdeallay = findViewById(R.id.bestdeallay);
        if (userData.getId().equalsIgnoreCase(data.getVendor_id()))
            bestdeallay.setVisibility(View.GONE);
        else
            bestdeallay.setVisibility(View.VISIBLE);

        findViewById(R.id.bestdeallay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductBestQuoteActivity.class);
                intent.putExtra("name", data.getName());
                intent.putExtra("vendorname", data.getAbout_the_seller().getFirstname());
                intent.putExtra("mobile", data.getAbout_the_seller().phone);
                if (data.getImage() != null && data.getImage().size() > 0)
                    intent.putExtra("image", data.getImage().get(0));
                intent.putExtra("category_id", data.getCategory_id());
                intent.putExtra("sub_category_id", data.sub_category_id);
                intent.putExtra("product_id", data.getId());
                intent.putExtra("vendor_id", data.getVendor_id());
                startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(data.is_fav)) {
            if (data.is_fav.equalsIgnoreCase("true"))
                hearticon.setImageResource(R.drawable.ic_heart_selected);
            else
                hearticon.setImageResource(R.drawable.ic_heart_unselected);
        }

        whatsappicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String userid = userData.getId();
                    String receiverId = data.getVendor_id();
                    String nodeid = "";
                    if (Integer.parseInt(userid) > Integer.parseInt(receiverId)) {
                        nodeid = userid + "_" + receiverId;
                    } else {
                        nodeid = receiverId + "_" + userid;
                    }
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("receiverId", data.getVendor_id().equalsIgnoreCase(userData.getId())
                            ? userData.getId() : data.getVendor_id());
                    intent.putExtra("user_id", userData.getId());
                    intent.putExtra("nodeid", nodeid);
                    intent.putExtra("receiverName", data.getAbout_the_seller().getFirstname() + " " +
                            data.getAbout_the_seller().getLast_seen());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        callappicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (data.is_payment.equalsIgnoreCase("true")) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ProductDetailActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                                    100);
                        } else
                            calluser(data.getAbout_the_seller().phone);
                    } else {
                        AppUtils.showalert(ProductDetailActivity.this, "You can't make a call to this vendor", false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (data.is_payment.equalsIgnoreCase("true"))
            locationlay.setVisibility(View.VISIBLE);
        else
            locationlay.setVisibility(View.GONE);
    }

    private void calluser(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void favunfav(Map<String, String> params, String isfav, int pos) {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getApplicationContext());
            Call<BaseResponse> call = apiInterface.favorite(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                                recommendedProducts.get(pos).setIs_fav(isfav.equalsIgnoreCase("true") ? "false" : "true");
                                adapter.notifyDataSetChanged();
                            } else {
                                AppUtils.showalert(ProductDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ProductDetailActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(ProductDetailActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void share() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, data.getName());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(data.getDescription()) + "\n\nVendor Details\n\n" + " Mobile: " + data.getAbout_the_seller().phone + "\nLocation: " + data.getLocation() + "\nDownload app using below mentioned link\n" +
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        ProductDetailActivity.this.startActivity(Intent.createChooser(sharingIntent, "Choose.."));

//        if (data.getImage() != null && data.getImage().size() > 0) {
//            progressbar.setVisibility(View.VISIBLE);
//            Picasso.get().load(data.getImage().get(0)).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    progressbar.setVisibility(View.GONE);
//                    if (bitmap != null) {
//                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                        sharingIntent.setType("text/plain");
//                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, data.getName());
//                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(data.getDescription()) + "\n\n" +
//                                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//                        sharingIntent.putExtra(Intent.EXTRA_STREAM, bitmap);
//                        ProductDetailActivity.this.startActivity(Intent.createChooser(sharingIntent, "Share"));
//                    }
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                    progressbar.setVisibility(View.GONE);
//                    e.printStackTrace();
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, data.getName());
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(data.getDescription()) + "\n\n" +
//                            "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//                    ProductDetailActivity.this.startActivity(Intent.createChooser(sharingIntent, "Choose.."));
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            });
//        } else {
//            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, data.getName());
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(data.getDescription()) + "\n\n" +
//                    "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//            ProductDetailActivity.this.startActivity(Intent.createChooser(sharingIntent, "Choose.."));
//        }

    }
}