package com.ubi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.SearchAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.SearchData;
import com.ubi.android.models.SearchResponse;
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

public class SearchActivity extends AppCompatActivity {

    final int REQUEST_CODE_SPEECH_INPUT = 101;
    EditText searchet;
    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<SearchData> data = new ArrayList<>();
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
    }

    private void init() {
        progressbar = findViewById(R.id.progressbar);
        searchet = findViewById(R.id.searchet);
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
                    Toast.makeText(SearchActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.VERTICAL, false));
//        recyclerview.addItemDecoration(new DividerItemDecoration(recyclerview.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new SearchAdapter(this, data);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.hearticon:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("product_id", data.get(pos).getId());
                        favunfav(params, data.get(pos).is_fav, pos);
                        break;
                    case R.id.mainlay:
                        Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", data.get(pos).getId());
                        startActivity(intent);
                        break;
                }

            }
        });
        recyclerview.setAdapter(adapter);
        searchet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (!TextUtils.isEmpty(str)) {
                    search(str);
                } else {
                    data.clear();
                    adapter.notifyDataSetChanged();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            data.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            }
        });

        if (getIntent().hasExtra("str")) {
            search(getIntent().getStringExtra("str"));
            searchet.setText(getIntent().getStringExtra("str"));
        }
    }

    private void search(String str) {
        data.clear();
        adapter.notifyDataSetChanged();
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        params.put("str", str);
        getSearchData(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                searchet.setText(
                        Objects.requireNonNull(result).get(0));
            }
        }
    }

    private void getSearchData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<SearchResponse> call = apiInterface.search(token, params);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    data.addAll(response.body().payload);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(SearchActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(SearchActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(SearchActivity.this);
        }
    }

    private void favunfav(Map<String, String> params, String isfav, int pos) {
        if (AppUtils.isConnectingToInternet(SearchActivity.this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(SearchActivity.this);
            Call<BaseResponse> call = apiInterface.favorite(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {

                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                Toast.makeText(SearchActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                                data.get(pos).is_fav = isfav.equalsIgnoreCase("true") ? "false" : "true";
                                adapter.notifyDataSetChanged();
                            } else {
                                AppUtils.showalert(SearchActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(SearchActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(SearchActivity.this);
        }
    }
}