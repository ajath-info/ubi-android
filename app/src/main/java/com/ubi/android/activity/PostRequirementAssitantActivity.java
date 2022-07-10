package com.ubi.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.PostRequirementType;
import com.ubi.android.models.PostRequirementtypeResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRequirementAssitantActivity extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<>();
    ArrayList<PostRequirementType> originallist = new ArrayList<>();
    AppCompatSpinner dropdown;
    String type;
    EditText qty, require;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_requirement_assitant);
        String productname = getIntent().getStringExtra("productname");
        TextView titletv = findViewById(R.id.titletv);
        UserData data = AppPreferences.getInstance().getUserData(getApplicationContext());
        titletv.setText("Hi  " + data.getFirst_name() + " " + data.getLast_name() + "! I can help you connect to verified suppliers for " + productname);
        qty = findViewById(R.id.qty);
        require = findViewById(R.id.require);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dropdown = findViewById(R.id.spinner1);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (originallist.size() > 0)
                    type = originallist.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getPostRequirementData();
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strqty = qty.getText().toString();
                String strrequire = require.getText().toString();
                if (TextUtils.isEmpty(strrequire)) {
                    AppUtils.showalert(PostRequirementAssitantActivity.this, "Please your requirement", false);
                } else if (TextUtils.isEmpty(strqty)) {
                    AppUtils.showalert(PostRequirementAssitantActivity.this, "Please enter quantity", false);
                } else {
                    submitdata(strqty, strrequire);
                }
            }
        });
    }

    private void getPostRequirementData() {
        if (AppUtils.isConnectingToInternet(this)) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<PostRequirementtypeResponse> call = apiInterface.postRequirementType(token);
            call.enqueue(new Callback<PostRequirementtypeResponse>() {
                @Override
                public void onResponse(Call<PostRequirementtypeResponse> call, Response<PostRequirementtypeResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload.size() > 0) {
                                    originallist.addAll(response.body().payload);
                                    for (PostRequirementType data :
                                            response.body().payload) {
                                        list.add(data.name);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PostRequirementAssitantActivity.this, android.R.layout.simple_spinner_dropdown_item, list);
                                    dropdown.setAdapter(adapter);
                                }
                            } else {
                                AppUtils.showalert(PostRequirementAssitantActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(PostRequirementAssitantActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PostRequirementtypeResponse> call, Throwable t) {
                    call.cancel();

                }
            });
        } else {
            AppUtils.Nointernetalert(PostRequirementAssitantActivity.this);
        }
    }

    private void submitdata(String quantity, String strrequire) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));
        params.put("quantity", quantity);
        params.put("type", strrequire);
        getSearchData(params);
    }

    private void getSearchData(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            ProgressDialog dialog = new ProgressDialog(PostRequirementAssitantActivity.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Please wait...");
            dialog.show();

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<BaseResponse> call = apiInterface.postRequirement(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        dialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                AppUtils.showalert(PostRequirementAssitantActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(PostRequirementAssitantActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    call.cancel();
                    dialog.dismiss();
                    t.printStackTrace();
                }
            });
        } else {
            AppUtils.Nointernetalert(PostRequirementAssitantActivity.this);
        }
    }
}