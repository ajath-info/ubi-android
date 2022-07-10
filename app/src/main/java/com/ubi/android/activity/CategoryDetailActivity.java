package com.ubi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.CategoryLookingAdapter;
import com.ubi.android.adapters.HotelSuggestionAdapter;
import com.ubi.android.adapters.PopularDestinationAdapter;
import com.ubi.android.adapters.SupplierAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.CategoryLookingModel;
import com.ubi.android.models.CategoryPageResponse;
import com.ubi.android.models.ImportantSupplies;
import com.ubi.android.models.PopularDestinationModel;
import com.ubi.android.models.SuggestionModel;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {
    final int REQUEST_CODE_SPEECH_INPUT = 101;
    CategoryLookingAdapter catlookingadapter;
    RecyclerView recyclerview, destrecyclerview, supplyrecyclerview, hotelsuggestionrecyclerview;
    ArrayList<CategoryLookingModel> categoriesModels = new ArrayList<>();
    PopularDestinationAdapter popularDestinationAdapter;
    ArrayList<PopularDestinationModel> popularDestinationModels = new ArrayList<>();
    ArrayList<ImportantSupplies> supplies = new ArrayList<>();
    SupplierAdapter supplierAdapter;
    ArrayList<SuggestionModel> suggestionModels = new ArrayList<>();
    HotelSuggestionAdapter hotelSuggestionAdapter;
    ProgressBar progressbar;
    TextView titletv;
    LinearLayout lookinglay, popularlay, importantlay, hotellay;
    TextView popularviewalltv, importantsupplyviewalltv, hotelsuggesviewalltv;
    TextView searchet, suggestiontxt;
    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        init();
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
    }

    private void init() {
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category_id", getIntent().getStringExtra("category_id"));
                getSearchData(params);
            }
        });
        suggestiontxt = findViewById(R.id.suggestiontxt);
        findViewById(R.id.categoryviewalltv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CategoriesViewAllActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                startActivity(intent);
            }
        });
        searchet = findViewById(R.id.searchet);
        searchet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryDetailActivity.this, SearchActivity.class));
            }
        });
        popularviewalltv = findViewById(R.id.popularviewalltv);
        popularviewalltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryDetailActivity.this, PopularDestinationActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                startActivity(intent);
            }
        });
        importantsupplyviewalltv = findViewById(R.id.importantsupplyviewalltv);
        importantsupplyviewalltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryDetailActivity.this, ImportantSuppliesViewAllActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                startActivity(intent);
            }
        });
        hotelsuggesviewalltv = findViewById(R.id.hotelsuggesviewalltv);
        hotelsuggesviewalltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryDetailActivity.this, SuggestionViewActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                startActivity(intent);
            }
        });
        hotellay = findViewById(R.id.hotellay);
        importantlay = findViewById(R.id.importantlay);
        popularlay = findViewById(R.id.popularlay);
        lookinglay = findViewById(R.id.lookinglay);
        titletv = findViewById(R.id.titletv);
        titletv.setText(getIntent().getStringExtra("title"));
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        catlookingadapter = new CategoryLookingAdapter(this, categoriesModels);
        catlookingadapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(CategoryDetailActivity.this, SubCategoryDetailActivity.class);
                intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                intent.putExtra("sub_category_id", categoriesModels.get(pos).id);
                intent.putExtra("title", categoriesModels.get(pos).name);
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(catlookingadapter);

        destrecyclerview = findViewById(R.id.destrecyclerview);
        destrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        popularDestinationAdapter = new PopularDestinationAdapter(this, popularDestinationModels);
        popularDestinationAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(CategoryDetailActivity.this, ProductListActivity.class);
                intent.putExtra("state_id", popularDestinationModels.get(pos).state_id);
                intent.putExtra("title", popularDestinationModels.get(pos).name);
                intent.putExtra("type", "populardestination");
                startActivity(intent);
            }
        });
        destrecyclerview.setAdapter(popularDestinationAdapter);

        supplyrecyclerview = findViewById(R.id.supplyrecyclerview);
        supplyrecyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        supplierAdapter = new SupplierAdapter(this, supplies);
        supplierAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(CategoryDetailActivity.this, ImportantSupplyProductActivity.class);
                intent.putExtra("important_supply_id", supplies.get(pos).getId());
                intent.putExtra("title", supplies.get(pos).getName());
                startActivity(intent);
            }
        });
        supplyrecyclerview.setAdapter(supplierAdapter);

        hotelsuggestionrecyclerview = findViewById(R.id.hotelsuggestionrecyclerview);
        hotelsuggestionrecyclerview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        hotelSuggestionAdapter = new HotelSuggestionAdapter(this, suggestionModels);
        hotelSuggestionAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.hearticon:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("product_id", suggestionModels.get(pos).getProduct_id());
                        favunfav(params, suggestionModels.get(pos).getIs_fav(), pos);
                        break;
                    case R.id.mainlay:
//                        Intent intent = new Intent(CategoryDetailActivity.this, ProductListActivity.class);
//                        intent.putExtra("hotel_stay_id", suggestionModels.get(pos).getProduct_id());
//                        intent.putExtra("title", suggestionModels.get(pos).getName());
//                        intent.putExtra("type", "hotel");
//                        startActivity(intent);
                        Intent intent = new Intent(CategoryDetailActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", suggestionModels.get(pos).getProduct_id());
                        startActivity(intent);
                        break;
                }

            }
        });
        hotelsuggestionrecyclerview.setAdapter(hotelSuggestionAdapter);

        Map<String, String> params = new HashMap<String, String>();
//        params.put("page", "1");
//        params.put("limit", "100");
        params.put("category_id", getIntent().getStringExtra("category_id"));
        getSearchData(params);

        RelativeLayout miclay = findViewById(R.id.miclay);
        miclay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search by speak");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void getSearchData(Map<String, String> params) {
        lookinglay.setVisibility(View.GONE);
        popularlay.setVisibility(View.GONE);
        importantlay.setVisibility(View.GONE);
        hotellay.setVisibility(View.GONE);

        categoriesModels.clear();
        catlookingadapter.notifyDataSetChanged();
        popularDestinationModels.clear();
        popularDestinationAdapter.notifyDataSetChanged();
        supplies.clear();
        supplierAdapter.notifyDataSetChanged();
        suggestionModels.clear();
        hotelSuggestionAdapter.notifyDataSetChanged();

        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<CategoryPageResponse> call = apiInterface.getCategoryPage(token, params);
            call.enqueue(new Callback<CategoryPageResponse>() {
                @Override
                public void onResponse(Call<CategoryPageResponse> call, Response<CategoryPageResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        swiperefresh.setRefreshing(false);
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    suggestiontxt.setText(response.body().payload.category_name + " Suggestions");
                                    if (response.body().payload.looking_for.size() > 0) {
                                        categoriesModels.addAll(response.body().payload.looking_for);
                                        catlookingadapter.notifyDataSetChanged();
                                        lookinglay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.popular_destinations.size() > 0) {
                                        popularDestinationModels.addAll(response.body().payload.popular_destinations);
                                        popularDestinationAdapter.notifyDataSetChanged();
                                        popularlay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.category_important_supplies.size() > 0) {
                                        supplies.addAll(response.body().payload.category_important_supplies);
                                        supplierAdapter.notifyDataSetChanged();
                                        importantlay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.suggestions.size() > 0) {
                                        suggestionModels.addAll(response.body().payload.suggestions);
                                        hotelSuggestionAdapter.notifyDataSetChanged();
                                        hotellay.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                AppUtils.showalert(CategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(CategoryDetailActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CategoryPageResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(CategoryDetailActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("str", Objects.requireNonNull(result).get(0));
                startActivity(intent);
            }
        }
    }

    private void favunfav(Map<String, String> params, String isfav, int pos) {
        if (AppUtils.isConnectingToInternet(CategoryDetailActivity.this)) {
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
                                suggestionModels.get(pos).setIs_fav(isfav.equalsIgnoreCase("true") ? "false" : "true");
                                hotelSuggestionAdapter.notifyDataSetChanged();
                            } else if (response.body().code == 3) {
                                Toast.makeText(CategoryDetailActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                AppUtils.clearcache(CategoryDetailActivity.this);
                            } else {
                                AppUtils.showalert(CategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(CategoryDetailActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(CategoryDetailActivity.this);
        }
    }
}