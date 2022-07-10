package com.ubi.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductBestQuoteActivity extends AppCompatActivity {

    RelativeLayout nametag, approxpricetag, requesttag, moredetailstag, sharepictag, pleasereplytag;
    TextView nametaglabel, approxlabel, requesquotelabel, sharepiclabel, pleasereplylabel, mordetaillabel;
    EditText messagtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_best_quote);
        nametaglabel = findViewById(R.id.nametaglabel);
        approxlabel = findViewById(R.id.approxlabel);
        requesquotelabel = findViewById(R.id.requesquotelabel);
        sharepiclabel = findViewById(R.id.sharepiclabel);
        pleasereplylabel = findViewById(R.id.pleasereplylabel);
        mordetaillabel = findViewById(R.id.mordetaillabel);

        nametag = findViewById(R.id.nametag);
        onclicktags(nametag, nametaglabel, "1");

        approxpricetag = findViewById(R.id.approxpricetag);
        onclicktags(approxpricetag, approxlabel, "2");

        requesttag = findViewById(R.id.requesttag);
        onclicktags(requesttag, requesquotelabel, "3");

        moredetailstag = findViewById(R.id.moredetailstag);
        onclicktags(moredetailstag, mordetaillabel, "4");

        sharepictag = findViewById(R.id.sharepictag);
        onclicktags(sharepictag, sharepiclabel, "5");

        pleasereplytag = findViewById(R.id.pleasereplytag);
        onclicktags(pleasereplytag, pleasereplylabel, "6");

        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try {
            TextView nametaglabel = findViewById(R.id.nametaglabel);
            nametaglabel.setText("Hi " + getIntent().getStringExtra("vendorname"));
            String name = getIntent().getStringExtra("name");
            TextView titletv = findViewById(R.id.titletv);
            titletv.setText("I am interested in " + name);
            if (getIntent().hasExtra("image")) {
                String image = getIntent().getStringExtra("image");
                Log.d(ProductBestQuoteActivity.class.getName(), image);
                if (!TextUtils.isEmpty(image)) {
                    ImageView imageview = findViewById(R.id.imageview);
                    Picasso.get().load(image).into(imageview);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.callnowlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (getIntent().hasExtra("mobile")) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ProductBestQuoteActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                                    100);
                        } else
                            calluser(getIntent().getStringExtra("mobile"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        messagtv = findViewById(R.id.messagtv);
        findViewById(R.id.sendlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strmessagtv = messagtv.getText().toString();
                if (TextUtils.isEmpty(strmessagtv)) {
                    AppUtils.showalert(ProductBestQuoteActivity.this, "Please enter your message", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("category_id", getIntent().getStringExtra("category_id"));
                    params.put("sub_category_id", getIntent().getStringExtra("sub_category_id"));
                    params.put("product_id", getIntent().getStringExtra("product_id"));
                    params.put("type", type);
                    params.put("message", strmessagtv);
                    params.put("vendor_id", getIntent().getStringExtra("vendor_id"));
                    postcomment(params);
                }
            }
        });
    }

    String type;

    private void calluser(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void onclicktags(RelativeLayout layout, TextView textView, String tag) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tag.equalsIgnoreCase("1")) {
                    type = tag;
                    reset();
                    layout.setBackgroundResource(R.drawable.bestquotetagbgselected);
                    textView.setTextColor(getResources().getColor(R.color.white));
                } else {
                    try {
                        UserData user = AppPreferences.getInstance().getUserData(getApplicationContext());
                        if (!user.getId().equalsIgnoreCase(getIntent().getStringExtra("vendor_id"))) {
                            String userid = user.getId();
                            String receiverId = getIntent().getStringExtra("vendor_id");
                            String nodeid = "";
                            if (Integer.parseInt(userid) > Integer.parseInt(receiverId)) {
                                nodeid = userid + "_" + receiverId;
                            } else {
                                nodeid = receiverId + "_" + userid;
                            }
                            Log.d(ProductBestQuoteActivity.class.getName(), nodeid);
                            Intent intent = new Intent(ProductBestQuoteActivity.this, ChatActivity.class);
                            intent.putExtra("receiverId", getIntent().getStringExtra("vendor_id"));
                            intent.putExtra("nodeid", nodeid);
                            intent.putExtra("receiverName", getIntent().getStringExtra("vendorname"));
                            startActivity(intent);
                        } else {
                            AppUtils.showalert(ProductBestQuoteActivity.this, "You can't initiate chat, as vendor and user are same", false);
                        }
                    } catch (Exception e) {
                        AppUtils.showalert(ProductBestQuoteActivity.this, "Something went wrong while init the chat", false);
                    }
                }
            }
        });

    }

    private void reset() {
        nametag.setBackgroundResource(R.drawable.bestquotetagbg);
        approxpricetag.setBackgroundResource(R.drawable.bestquotetagbg);
        requesttag.setBackgroundResource(R.drawable.bestquotetagbg);
        moredetailstag.setBackgroundResource(R.drawable.bestquotetagbg);
        sharepictag.setBackgroundResource(R.drawable.bestquotetagbg);
        pleasereplytag.setBackgroundResource(R.drawable.bestquotetagbg);

        nametaglabel.setTextColor(getResources().getColor(R.color.black));
        approxlabel.setTextColor(getResources().getColor(R.color.black));
        requesquotelabel.setTextColor(getResources().getColor(R.color.black));
        sharepiclabel.setTextColor(getResources().getColor(R.color.black));
        pleasereplylabel.setTextColor(getResources().getColor(R.color.black));
        mordetaillabel.setTextColor(getResources().getColor(R.color.black));
    }

    private void postcomment(Map<String, String> params) {
        ProgressDialog dialog = new ProgressDialog(ProductBestQuoteActivity.this);
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getApplicationContext());
            Call<BaseResponse> call = apiInterface.productComments(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        dialog.dismiss();
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                messagtv.setText("");
                                reset();
//                                Toast.makeText(getApplicationContext(), response.body().message, Toast.LENGTH_SHORT).show();
                                AppUtils.showalert(ProductBestQuoteActivity.this, response.body().message, false);

                            } else {
                                AppUtils.showalert(ProductBestQuoteActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(ProductBestQuoteActivity.this, response.message(), false);
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
                    AppUtils.showalert(ProductBestQuoteActivity.this, t.getMessage(), false);
                }
            });
        } else {
            AppUtils.Nointernetalert(ProductBestQuoteActivity.this);
        }
    }
}
