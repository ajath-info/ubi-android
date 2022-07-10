package com.ubi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.R;
import com.ubi.android.utils.AppUtils;


public class PostAdThirdStepActivity extends AppCompatActivity {

    TextView locationtxt;
    EditText pricetv, fulllocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_third_step);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fulllocation = findViewById(R.id.fulllocation);
        pricetv = findViewById(R.id.pricetv);
        locationtxt = findViewById(R.id.locationtxt);
        findViewById(R.id.locationlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostAdThirdStepActivity.this, LocationsActivity.class);
                intent.putExtra("isedit", false);
                startActivityForResult(intent, 101);
            }
        });
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strpricetv = pricetv.getText().toString();
                String strfulllocation = fulllocation.getText().toString();
                if (TextUtils.isEmpty(strpricetv)) {
                    AppUtils.showalert(PostAdThirdStepActivity.this, "Please enter price", false);
                } else if (TextUtils.isEmpty(location)) {
                    AppUtils.showalert(PostAdThirdStepActivity.this, "Please select location", false);
                } else if (TextUtils.isEmpty(strfulllocation)) {
                    AppUtils.showalert(PostAdThirdStepActivity.this, "Please enter address", false);
                } else {
                    Intent intent = new Intent(PostAdThirdStepActivity.this, PostAdFourthStepActivity.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("price", strpricetv);
                    intent.putExtra("location", strfulllocation + " " + location);
                    intent.putExtra("state", state);
                    intent.putExtra("counrty", counrty);
                    startActivityForResult(intent, 102);
                }
            }
        });
    }

    String location, counrty, state;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            location = data.getStringExtra("location");
            state = data.getStringExtra("state");
            counrty = data.getStringExtra("counrty");
            locationtxt.setText(location);
        } else if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}