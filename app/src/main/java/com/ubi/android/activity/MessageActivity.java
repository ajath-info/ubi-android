package com.ubi.android.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.Fragments.MessageFragment;
import com.ubi.android.Fragments.NotificationFragment;
import com.ubi.android.R;

public class MessageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        init();
    }

    private void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, new MessageFragment()).commit();
    }
}