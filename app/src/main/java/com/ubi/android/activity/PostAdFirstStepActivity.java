package com.ubi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.R;
import com.ubi.android.utils.AppUtils;


public class PostAdFirstStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_first_step);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        EditText titletv = findViewById(R.id.titletv);
        EditText desctv = findViewById(R.id.desctv);
        findViewById(R.id.applybtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strtitle = titletv.getText().toString();
                String strdesctv = desctv.getText().toString();
                if (TextUtils.isEmpty(strtitle)) {
                    AppUtils.showalert(PostAdFirstStepActivity.this, "Please enter title", false);
                } else if (TextUtils.isEmpty(strdesctv)) {
                    AppUtils.showalert(PostAdFirstStepActivity.this, "Please enter description", false);
                } else {
                    Intent intent = new Intent(PostAdFirstStepActivity.this, PostAdSecondStep.class);
                    intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
                    intent.putExtra("sub_category_id", getIntent().getStringExtra("sub_category_id"));
                    intent.putExtra("title", strtitle);
                    intent.putExtra("desc", strdesctv);
                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}