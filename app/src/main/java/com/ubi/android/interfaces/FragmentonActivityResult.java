package com.ubi.android.interfaces;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface FragmentonActivityResult {
    void onActivityResultV2(int requestCode, int resultCode,
                          @Nullable Intent data);
}
