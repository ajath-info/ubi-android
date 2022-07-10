package com.ubi.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ubi.android.MainActivity;
import com.ubi.android.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    LinearLayout englishlay, frenchlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        englishlay = findViewById(R.id.englishlay);
        englishlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languagePopup("en");
            }
        });
        frenchlay = findViewById(R.id.frenchlay);
        frenchlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languagePopup("fr");
            }
        });
    }

    private void languagePopup(String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LanguageActivity.this);
        builder.setTitle("Are you sure want to change language?");
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String languageToLoad = language;
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                dialog.dismiss();
//                rEditor.putString("language", languageToLoad);
//                rEditor.commit();


                Intent refresh = new Intent(LanguageActivity.this, MainActivity.class);
                startActivity(refresh);
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}