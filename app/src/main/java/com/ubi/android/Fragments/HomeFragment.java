package com.ubi.android.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.activity.AutoMobileViewallActivity;
import com.ubi.android.activity.CategoriesHomeAllActivity;
import com.ubi.android.activity.CategoryDetailActivity;
import com.ubi.android.activity.DealViewAllActivity;
import com.ubi.android.activity.HotelStayViewAllActivity;
import com.ubi.android.activity.ImportantSuppliesViewAllActivity;
import com.ubi.android.activity.ImportantSupplyProductActivity;
import com.ubi.android.activity.ProductDetailActivity;
import com.ubi.android.activity.ProductListActivity;
import com.ubi.android.activity.SearchActivity;
import com.ubi.android.activity.ShoppingViewAllActivity;
import com.ubi.android.adapters.AutoMobileAdapter;
import com.ubi.android.adapters.CategoryHomeAdapter;
import com.ubi.android.adapters.DealAdapter;
import com.ubi.android.adapters.HotelStayAdapter;
import com.ubi.android.adapters.ProductInterestAdapter;
import com.ubi.android.adapters.ShoppingAdapter;
import com.ubi.android.adapters.SupplierAdapter;
import com.ubi.android.adapters.TopBannerAdapter;
import com.ubi.android.interfaces.FragmentonActivityResult;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.AutomobileModel;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.CategoriesModel;
import com.ubi.android.models.DealsModel;
import com.ubi.android.models.HomeData;
import com.ubi.android.models.HomeResponse;
import com.ubi.android.models.HotelStay;
import com.ubi.android.models.ImportantSupplies;
import com.ubi.android.models.ProductInterest;
import com.ubi.android.models.ShoppingModel;
import com.ubi.android.models.TopBanners;
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

public class HomeFragment extends Fragment implements FragmentonActivityResult {
    final int REQUEST_CODE_SPEECH_INPUT = 101;
    RecyclerView catrecyclerview, suplierrecyclerview, dealrecyclerview, interestrecyclerview, shoppingrecyclerview, hotelrecyclerview, autorecyclerview;
    ViewPager topbannerpager;
    HomeData homeData;
    ProgressBar progressbar;
    DotsIndicator dots_indicator;
    CategoryHomeAdapter categoryHomeAdapter;
    SupplierAdapter supplierAdapter;
    DealAdapter dealAdapter;
    ArrayList<CategoriesModel> categoriesModels = new ArrayList<>();
    LinearLayout catlay, suplierlay, deallay, intrestlay, shoppinglay, hotelstaylay, autolay;
    ArrayList<ImportantSupplies> supplies = new ArrayList<>();
    ArrayList<DealsModel> dealsModels = new ArrayList<>();
    ProductInterestAdapter productInterestAdapter;
    ArrayList<ProductInterest> productInterests = new ArrayList<>();
    ShoppingAdapter shoppingAdapter;
    ArrayList<ShoppingModel> shoppingModels = new ArrayList<>();
    ArrayList<TopBanners> topBanners = new ArrayList<>();
    ArrayList<HotelStay> hotelStays = new ArrayList<>();
    HotelStayAdapter hotelStayAdapter;

    ArrayList<AutomobileModel> automobileModels = new ArrayList<>();
    AutoMobileAdapter autoMobileAdapter;
    SwipeRefreshLayout swipe;
    TextView searchet;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        getHomeData(params);
        return v;
    }

    private void init(View v) {
        swipe = v.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
                params.put("limit", "100");
                getHomeData(params);
            }
        });
        Log.d(HomeFragment.class.getName(), AppPreferences.getInstance().getToken(getActivity()));
        searchet = v.findViewById(R.id.searchet);
        searchet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        autolay = v.findViewById(R.id.autolay);
        hotelstaylay = v.findViewById(R.id.hotelstaylay);
        shoppinglay = v.findViewById(R.id.shoppinglay);
        intrestlay = v.findViewById(R.id.intrestlay);
        deallay = v.findViewById(R.id.deallay);
        catlay = v.findViewById(R.id.catlay);
        suplierlay = v.findViewById(R.id.suplierlay);
        progressbar = v.findViewById(R.id.progressbar);
        topbannerpager = v.findViewById(R.id.topbannerpager);
        dots_indicator = v.findViewById(R.id.dots_indicator);

        catrecyclerview = v.findViewById(R.id.catrecyclerview);
        catrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.HORIZONTAL, false));
        categoryHomeAdapter = new CategoryHomeAdapter(getActivity(), categoriesModels, false);
        categoryHomeAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
//                if (categoriesModels.get(pos).getName().equalsIgnoreCase("All")) {
////                    startActivity(new Intent(getActivity(), CategoriesViewAllActivity.class));
//                }
                Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
                intent.putExtra("category_id", categoriesModels.get(pos).getId());
                intent.putExtra("title", categoriesModels.get(pos).getName());
                startActivity(intent);
            }
        });
        catrecyclerview.setAdapter(categoryHomeAdapter);

        suplierrecyclerview = v.findViewById(R.id.suplierrecyclerview);
        suplierrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        supplierAdapter = new SupplierAdapter(getActivity(), supplies);
        supplierAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), ImportantSupplyProductActivity.class);
                intent.putExtra("important_supply_id", supplies.get(pos).getId());
                intent.putExtra("title", supplies.get(pos).getName());
                startActivity(intent);
            }
        });
        suplierrecyclerview.setAdapter(supplierAdapter);

        dealrecyclerview = v.findViewById(R.id.dealrecyclerview);
        dealrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        dealAdapter = new DealAdapter(getActivity(), dealsModels);
        dealAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("product_id", dealsModels.get(pos).getId());
                startActivity(intent);
            }
        });
        dealrecyclerview.setAdapter(dealAdapter);

        interestrecyclerview = v.findViewById(R.id.interestrecyclerview);
        interestrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        productInterestAdapter = new ProductInterestAdapter(getActivity(), productInterests);
        productInterestAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.hearticon:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("product_id", productInterests.get(pos).getId());
                        favunfav(params, productInterests.get(pos).getIs_fav(), pos);
                        break;
                    case R.id.mainlay:
                        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                        intent.putExtra("product_id", productInterests.get(pos).getId());
                        startActivity(intent);
                        break;
                }

            }
        });
        interestrecyclerview.setAdapter(productInterestAdapter);

        shoppingrecyclerview = v.findViewById(R.id.shoppingrecyclerview);
        shoppingrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        shoppingAdapter = new ShoppingAdapter(getActivity(), shoppingModels);
        shoppingAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("shopping_id", shoppingModels.get(pos).getId());
                intent.putExtra("title", shoppingModels.get(pos).getName());
                intent.putExtra("type", "shopping");
                startActivity(intent);
            }
        });
        shoppingrecyclerview.setAdapter(shoppingAdapter);

        hotelrecyclerview = v.findViewById(R.id.hotelrecyclerview);
        hotelrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        hotelStayAdapter = new HotelStayAdapter(getActivity(), hotelStays);
        hotelStayAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("hotel_stay_id", hotelStays.get(pos).getId());
                intent.putExtra("title", hotelStays.get(pos).getName());
                intent.putExtra("type", "hotel");
                startActivity(intent);
            }
        });
        hotelrecyclerview.setAdapter(hotelStayAdapter);

        autorecyclerview = v.findViewById(R.id.autorecyclerview);
        autorecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        autoMobileAdapter = new AutoMobileAdapter(getActivity(), automobileModels);
        autoMobileAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("automobile_id", automobileModels.get(pos).getId());
                intent.putExtra("title", automobileModels.get(pos).getName());
                intent.putExtra("type", "automobile");
                startActivity(intent);
            }
        });
        autorecyclerview.setAdapter(autoMobileAdapter);

        TextView suppliesviewalltv = v.findViewById(R.id.suppliesviewalltv);
        suppliesviewalltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ImportantSuppliesViewAllActivity.class));
            }
        });

        RelativeLayout miclay = v.findViewById(R.id.miclay);
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
                    getActivity().startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        TextView dealsviewalltv = v.findViewById(R.id.dealsviewalltv);
        dealsviewalltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), DealViewAllActivity.class));
            }
        });

        TextView shoppingviewallet = v.findViewById(R.id.shoppingviewallet);
        shoppingviewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ShoppingViewAllActivity.class));
            }
        });
        TextView hotelviewallet = v.findViewById(R.id.hotelviewallet);
        hotelviewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), HotelStayViewAllActivity.class));
            }
        });

        TextView autoviewallet = v.findViewById(R.id.autoviewallet);
        autoviewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), AutoMobileViewallActivity.class));
            }
        });

        TextView catviewallet = v.findViewById(R.id.catviewallet);
        catviewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), CategoriesHomeAllActivity.class));
            }
        });
    }

    TopBannerAdapter adapter;

    private void setbanner() {
        adapter = new TopBannerAdapter(getActivity(), topBanners);
        topbannerpager.setAdapter(adapter);
        dots_indicator.setViewPager(topbannerpager);
    }

    private void getHomeData(Map<String, String> params) {
        categoriesModels.clear();
        supplies.clear();
        dealsModels.clear();
        productInterests.clear();
        shoppingModels.clear();
        hotelStays.clear();
        automobileModels.clear();
        topBanners.clear();
        setbanner();
        categoryHomeAdapter.notifyDataSetChanged();
        catlay.setVisibility(View.GONE);
        suplierlay.setVisibility(View.GONE);
        deallay.setVisibility(View.GONE);
        intrestlay.setVisibility(View.GONE);
        shoppinglay.setVisibility(View.GONE);
        hotelstaylay.setVisibility(View.GONE);
        autolay.setVisibility(View.GONE);


        if (AppUtils.isConnectingToInternet(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Log.d(HomeFragment.class.getName(), token);
            Call<HomeResponse> call = apiInterface.getHomePage(token, params);
            call.enqueue(new Callback<HomeResponse>() {
                @Override
                public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        swipe.setRefreshing(false);
                        if (getActivity() != null) {
                            progressbar.setVisibility(View.GONE);
                            if (response.code() == 200) {
                                if (response.body().code == 1) {
                                    homeData = response.body().payload;
                                    if (response.body().payload.getCategories().size() > 0) {
                                        categoriesModels.addAll(response.body().payload.getCategories());
//                                    CategoriesModel model = new CategoriesModel();
//                                    model.setIcon("");
//                                    model.islocal = true;
//                                    model.setName("All");
//                                    categoriesModels.add(model);
                                        categoryHomeAdapter.notifyDataSetChanged();
                                        catlay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getImportant_supplies().size() > 0) {
                                        supplies.addAll(response.body().payload.getImportant_supplies());
                                        supplierAdapter.notifyDataSetChanged();
                                        suplierlay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getDeals().size() > 0) {
                                        dealsModels.addAll(response.body().payload.getDeals());
                                        dealAdapter.notifyDataSetChanged();
                                        deallay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getProduct_interest().size() > 0) {
                                        productInterests.addAll(response.body().payload.getProduct_interest());
                                        productInterestAdapter.notifyDataSetChanged();
                                        intrestlay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getShopping().size() > 0) {
                                        shoppingModels.addAll(response.body().payload.getShopping());
                                        shoppingAdapter.notifyDataSetChanged();
                                        shoppinglay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getHotelStay().size() > 0) {
                                        hotelStays.addAll(response.body().payload.getHotelStay());
                                        hotelStayAdapter.notifyDataSetChanged();
                                        hotelstaylay.setVisibility(View.VISIBLE);
                                    }

                                    if (response.body().payload.getAutomobile().size() > 0) {
                                        automobileModels.addAll(response.body().payload.getAutomobile());
                                        autoMobileAdapter.notifyDataSetChanged();
                                        autolay.setVisibility(View.VISIBLE);
                                    }
                                    if (getActivity() != null) {
                                        topBanners.addAll(homeData.getTop_banners());
                                        setbanner();
                                    }

                                } else if (response.body().code == 3) {
                                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                                    if (getActivity() != null)
                                        AppUtils.clearcache(getActivity());
                                } else {
                                    AppUtils.showalert(getActivity(), response.body().message, false);
                                }
                            } else {
                                AppUtils.showalert(getActivity(), response.message(), false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<HomeResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }


    @Override
    public void onActivityResultV2(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
//                searchet.setText(
//                        Objects.requireNonNull(result).get(0));
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("str", Objects.requireNonNull(result).get(0));
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            AppUtils.showalert(getActivity(), "Something went wrong", false);
        }
    }

    private void favunfav(Map<String, String> params, String isfav, int pos) {
        if (AppUtils.isConnectingToInternet(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<BaseResponse> call = apiInterface.favorite(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (getActivity() != null) {
                            progressbar.setVisibility(View.GONE);
                            if (response.code() == 200) {
                                if (response.body().code == 1) {
                                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                                    productInterests.get(pos).setIs_fav(isfav.equalsIgnoreCase("true") ? "false" : "true");
                                    productInterestAdapter.notifyDataSetChanged();
                                } else {
                                    AppUtils.showalert(getActivity(), response.body().message, false);
                                }
                            } else {
                                AppUtils.showalert(getActivity(), response.message(), false);
                            }
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
            AppUtils.Nointernetalert(getActivity());
        }
    }
}
