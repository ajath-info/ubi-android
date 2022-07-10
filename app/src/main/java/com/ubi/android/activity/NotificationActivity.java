package com.ubi.android.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.Fragments.NotificationFragment;
import com.ubi.android.R;

public class NotificationActivity extends AppCompatActivity {


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
        TextView titletv = findViewById(R.id.titletv);
        titletv.setText("Notification");
        init();
    }

    private void init() {
        NotificationFragment fragment = new NotificationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, fragment).commit();
        findViewById(R.id.deletalltv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NotificationActivity.this)
                        .setTitle("Delete Notification")
                        .setMessage("Are you sure you want to delete all notification?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.deleteall();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }
}