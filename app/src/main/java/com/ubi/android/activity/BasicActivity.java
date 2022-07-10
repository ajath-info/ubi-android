package com.ubi.android.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ubi.android.R;
import com.ubi.android.utils.AppUtils;

public class BasicActivity extends AppCompatActivity {

    public AlertDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = AppUtils.Progress(this);

    }
}