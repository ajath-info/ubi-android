package com.ubi.android.utils;

import android.app.Application;

//import com.google.firebase.FirebaseApp;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(getApplicationContext());
    }
}
