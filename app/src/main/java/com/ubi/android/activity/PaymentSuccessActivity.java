package com.ubi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.MainActivity;
import com.ubi.android.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    ImageView image;
    TextView homebtn, titletv, subtitle;
    boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        isSuccess = getIntent().getBooleanExtra("isSuccess", false);
        titletv = findViewById(R.id.titletv);
        subtitle = findViewById(R.id.subtitle);
        image = findViewById(R.id.image);
        homebtn = findViewById(R.id.homebtn);
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        if (!isSuccess) {
            titletv.setText("Failed");
            subtitle.setText("Your payment has failed");
            image.setImageResource(R.drawable.error);
        }
    }
}