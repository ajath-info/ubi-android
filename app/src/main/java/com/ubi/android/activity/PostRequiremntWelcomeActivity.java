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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.AutoCompleteAdapter;
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

public class PostRequiremntWelcomeActivity extends AppCompatActivity {
    final int REQUEST_CODE_SPEECH_INPUT = 101;
    ArrayList<SearchData> originalsearchArrayList = new ArrayList<SearchData>();
    ArrayList<String> searchArrayList = new ArrayList<String>();
    AutoCompleteTextView autoCompleteTextView;
    ProgressBar progressbar;
    AutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_requiremnt_welcome);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, searchArrayList);
        autoCompleteTextView = findViewById(R.id.searchAutoComplete);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (!TextUtils.isEmpty(str)) {
                    search(str);
                } else {
                    searchArrayList.clear();
                    adapter.notifyDataSetChanged();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchArrayList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            }
        });
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchData data = originalsearchArrayList.get(i);
                Intent intent = new Intent(getApplicationContext(), PostRequirementAssitantActivity.class);
                intent.putExtra("product_id", data.getId());
                intent.putExtra("productname", data.getName());
                startActivityForResult(intent, 340);
            }
        });

        ImageView miclay = findViewById(R.id.miclay);
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
                    Toast.makeText(PostRequiremntWelcomeActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String name = autoCompleteTextView.getText().toString();
//                if (TextUtils.isEmpty(name)) {
//                    AppUtils.showalert(PostRequiremntWelcomeActivity.this, "Please enter something", false);
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), PostRequirementAssitantActivity.class);
//                    intent.putExtra("product_id", "0");
//                    intent.putExtra("productname", name);
//                    startActivityForResult(intent, 340);
//                }
            }
        });

    }

    private void search(String str) {
        searchArrayList.clear();
        adapter.notifyDataSetChanged();
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "100");
        params.put("str", str);
        getSearchData(params);
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
                                    originalsearchArrayList.addAll(response.body().payload);
                                    for (SearchData data :
                                            response.body().payload) {
                                        searchArrayList.add(data.getName());
                                    }

                                    adapter = new AutoCompleteAdapter(PostRequiremntWelcomeActivity.this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, searchArrayList);
                                    autoCompleteTextView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                AppUtils.showalert(PostRequiremntWelcomeActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(PostRequiremntWelcomeActivity.this, response.message(), false);
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
            AppUtils.Nointernetalert(PostRequiremntWelcomeActivity.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                autoCompleteTextView.setText(
                        Objects.requireNonNull(result).get(0));
            }
        } else if (requestCode == 340 && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }
}