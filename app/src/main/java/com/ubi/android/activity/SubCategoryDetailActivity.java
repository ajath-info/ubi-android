package com.ubi.android.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.BuildConfig;
import com.ubi.android.R;
import com.ubi.android.adapters.SubCategoryAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.SubCategoryDetailModel;
import com.ubi.android.models.SubCategoryResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;
import com.ubi.android.utils.PointerPopupWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<SubCategoryDetailModel> categoriesModels = new ArrayList<>();
    SubCategoryAdapter adapter;
    TextView titletv, nodata;
    TextView sorttext, filtertxt;
    View popupbg;
    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_detail);
        init();
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
    }

    private void share() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void init() {
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchdata();
            }
        });
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
        findViewById(R.id.shareimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
        filtertxt = findViewById(R.id.filtertxt);
        filtertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilterpopup();
            }
        });
        popupbg = findViewById(R.id.popupbg);
        sorttext = findViewById(R.id.sorttext);
        sorttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensortpopup();
            }
        });
        titletv = findViewById(R.id.titletv);
        nodata = findViewById(R.id.nodata);
        titletv.setText(getIntent().getStringExtra("title"));
        adapter = new SubCategoryAdapter(this, categoriesModels);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.hearticon:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("product_id", categoriesModels.get(pos).id);
                        favunfav(params, categoriesModels.get(pos).is_fav, pos);
                        break;
                    case R.id.mainlay:
                        Intent intent = new Intent(SubCategoryDetailActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", categoriesModels.get(pos).id);
                        startActivity(intent);
                        break;
                    case R.id.sellerrating: {
                        Intent intents = new Intent(getApplicationContext(), RatingActivity.class);
                        intents.putExtra("isseller", false);
                        intents.putExtra("product_id", categoriesModels.get(pos).id);
                        intents.putExtra("category_id", categoriesModels.get(pos).category_id);
                        intents.putExtra("sub_category_id", categoriesModels.get(pos).sub_category_id);
                        startActivity(intents);
                        break;
                    }
                }

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
        fetchdata();
    }

    private void fetchdata() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        params.put("category_id", getIntent().getStringExtra("category_id"));
        params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
        getProductDetails(params, 1);
    }

    private void getProductDetails(Map<String, String> params, int type) {
        if (AppUtils.isConnectingToInternet(this)) {
            categoriesModels.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<SubCategoryResponse> call = apiInterface.productListAll(token, params);
            if (type == 2)
                call = apiInterface.productShorting(token, params);
            else if (type == 3)
                call = apiInterface.productFilters(token, params);

            call.enqueue(new Callback<SubCategoryResponse>() {
                @Override
                public void onResponse(Call<SubCategoryResponse> call, Response<SubCategoryResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        swiperefresh.setRefreshing(false);
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        categoriesModels.addAll(response.body().payload);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                nodata.setText(response.body().message);
                                nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(SubCategoryDetailActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SubCategoryResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(SubCategoryDetailActivity.this);
        }
    }

    public void opensortpopup() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        PointerPopupWindow pop = new PointerPopupWindow(getApplicationContext(), (int) (width * 0.96));
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.sort_popup, null);
        TextView relvancetxt = customView.findViewById(R.id.relvancetxt);
        relvancetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(1);
            }
        });
        TextView ratingtxt = customView.findViewById(R.id.ratingtxt);
        ratingtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(2);
            }
        });
        TextView populartxt = customView.findViewById(R.id.populartxt);
        populartxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(3);
            }
        });
        TextView friendsratingtxt = customView.findViewById(R.id.friendsratingtxt);
        friendsratingtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(4);
            }
        });
        TextView highestpricetxt = customView.findViewById(R.id.highestpricetxt);
        highestpricetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(5);
            }
        });
        TextView lowestpricetxt = customView.findViewById(R.id.lowestpricetxt);
        lowestpricetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.dismiss();
                sortdata(6);
            }
        });

        popupbg.setVisibility(View.VISIBLE);

        pop.setContentView(customView);
//        pop.setPointerImageRes(R.drawable.ic_popup_pointer);
        pop.setBackgroundDrawable((getResources().getDrawable(R.drawable.popup)));
        pop.setMarginScreen(0);
        pop.showAsPointer(sorttext);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupbg.setVisibility(View.GONE);
            }
        });
    }

    private void sortdata(int shorting_type) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        params.put("category_id", getIntent().getStringExtra("category_id"));
        params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
        params.put("shorting_type", "" + shorting_type);
        getProductDetails(params, 2);
    }


    int datetype;
    TextView startdatetxt, enddate;

    public void openfilterpopup() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        PointerPopupWindow pop = new PointerPopupWindow(getApplicationContext(), (int) (width * 0.96));
        Date dateObj = new Date();
        dateObj.setTime(System.currentTimeMillis());
        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat apipostFormater = new SimpleDateFormat("yyyy-MM-dd");
        String newDateStr = postFormater.format(dateObj);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.filter_popup, null);
        startdatetxt = customView.findViewById(R.id.startdatetxt);
        startdatetxt.setText(newDateStr);
        startdatetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datetype = 1;
                com.ubi.android.utils.DatePicker picker = new com.ubi.android.utils.DatePicker();
                picker.show(getSupportFragmentManager(), "datepicker");
            }
        });
        stardate = apipostFormater.format(dateObj);
        strendate = apipostFormater.format(dateObj);
        enddate = customView.findViewById(R.id.enddate);
        enddate.setText(newDateStr);
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datetype = 2;
                com.ubi.android.utils.DatePicker picker = new com.ubi.android.utils.DatePicker();
                picker.show(getSupportFragmentManager(), "datepicker");
                if (startdatecal != null) {
                    picker.setMinDate(startdatecal);
                }
            }
        });
        popupbg.setVisibility(View.VISIBLE);

        TextView applybtn = customView.findViewById(R.id.applybtn);
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating = android.text.TextUtils.join(",", star_rating);
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
                params.put("limit", "1000");
                params.put("category_id", getIntent().getStringExtra("category_id"));
                params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
                params.put("check_in_from", stardate);
                params.put("check_in_to", strendate);
                params.put("star_rating", rating);
                params.put("budget_from", strbudgetstart);
                params.put("budget_to", strbudgetend);
                getProductDetails(params, 3);
                pop.dismiss();
            }
        });
        TextView fivetxt = customView.findViewById(R.id.fivetxt);
        onclickFilter(fivetxt, 1);
        TextView fourtxt = customView.findViewById(R.id.fourtxt);
        onclickFilter(fourtxt, 4);
        TextView threetxt = customView.findViewById(R.id.threetxt);
        onclickFilter(threetxt, 3);
        TextView twotxt = customView.findViewById(R.id.twotxt);
        onclickFilter(twotxt, 2);
        TextView onetext = customView.findViewById(R.id.onetext);
        onclickFilter(onetext, 1);

        TextView budgetone = customView.findViewById(R.id.budgetone);
        onclickFilterBudget(budgetone, 1);

        TextView budgettwo = customView.findViewById(R.id.budgettwo);
        onclickFilterBudget(budgettwo, 2);


        pop.setContentView(customView);
//        pop.setPointerImageRes(R.drawable.ic_popup_pointer);
        pop.setBackgroundDrawable((getResources().getDrawable(R.drawable.filterpopup)));
        pop.setMarginScreen(0);
        pop.showAsPointer(sorttext);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupbg.setVisibility(View.GONE);
            }
        });
    }

    private void onclickFilter(TextView textView, int type) {
        textView.setOnClickListener(new View.OnClickListener() {
            private boolean stateChanged;

            public void onClick(View view) {
                if (stateChanged) {
                    textView.setBackgroundResource(R.drawable.filtersortbg);
                    textView.setTextColor(getResources().getColor(R.color.filtertxt));
                    star_rating.remove("" + type);
                } else {
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setBackgroundResource(R.drawable.filtersortbg_h);
                    star_rating.add("" + type);
                }
                stateChanged = !stateChanged;
            }

        });
    }

    private void onclickFilterBudget(TextView textView, int type) {
        textView.setOnClickListener(new View.OnClickListener() {
            private boolean stateChanged;

            public void onClick(View view) {
                if (stateChanged) {
                    textView.setBackgroundResource(R.drawable.filtersortbg);
                    textView.setTextColor(getResources().getColor(R.color.filtertxt));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setBackgroundResource(R.drawable.filtersortbg_h);
                    star_rating.add("" + type);
                }
                stateChanged = !stateChanged;
                if (type == 1) {
                    strbudgetstart = "1";
                    strbudgetend = "500";
                } else {
                    strbudgetstart = "501";
                    strbudgetend = "1000";
                }
            }

        });
    }

    String stardate, strendate;
    String strbudgetstart = "1";
    String strbudgetend = "500";
    Calendar startdatecal;
    ArrayList<String> star_rating = new ArrayList<>();

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat apipostFormater = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate = postFormater.format(mCalendar.getTime());
        if (datetype == 1) {
            startdatecal = mCalendar;
            stardate = apipostFormater.format(mCalendar.getTime());
            startdatetxt.setText(selectedDate);
        } else {
            strendate = apipostFormater.format(mCalendar.getTime());
            ;
            enddate.setText(selectedDate);
        }
    }

    private void favunfav(Map<String, String> params, String isfav, int pos) {
        if (AppUtils.isConnectingToInternet(SubCategoryDetailActivity.this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(SubCategoryDetailActivity.this);
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
                                categoriesModels.get(pos).is_fav = isfav.equalsIgnoreCase("true") ? "false" : "true";
                                adapter.notifyDataSetChanged();
                            } else {
                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(SubCategoryDetailActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(SubCategoryDetailActivity.this);
        }
    }
}