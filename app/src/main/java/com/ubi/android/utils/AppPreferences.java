package com.ubi.android.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.google.gson.Gson;
import com.ubi.android.models.UserData;

import java.util.ArrayList;

/**
 * @author itgc
 */
public class AppPreferences {

    static AppPreferences instance;

    public AppPreferences() {

    }

    public static AppPreferences getInstance() {
        if (instance == null)
            instance = new AppPreferences();

        return instance;
    }

    public void Logout(Context ctx) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.clear().commit();

    }

    public int getAppVer(Context ctx, String s) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getInt(s, 0);
    }

    public void setAppVer(Context ctx, String a, int value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putInt(a, value);
        mPrefsEditor.commit();
    }


    public String getAddressDefaultID(Context ctx) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("addressDefaultID", "");
    }

    public void setAddressDefaultID(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("addressDefaultID", value);
        mPrefsEditor.commit();
    }


    public boolean isUserLoggedIn(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("Login", false);
    }


    public void setUserLoggedIn(Context ctx, Boolean value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("Login", value);
        mPrefsEditor.apply();
    }

    public UserData getUserData(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Gson gson = new Gson();
        String json = mPrefs.getString("userdata", "");
        UserData obj = gson.fromJson(json, UserData.class);
        return obj;
    }

    public void setUserData(Context ctx, UserData value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        mPrefsEditor.putString("userdata", json);
        mPrefsEditor.apply();
    }
//
//    public UserAddressResponse.AddressData getAddressData(Context ctx) {
//        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//        Gson gson = new Gson();
//        String json = mPrefs.getString("addressdata", "");
//        UserAddressResponse.AddressData obj = gson.fromJson(json, UserAddressResponse.AddressData.class);
//        return obj;
//    }

    public int getCompletedSteps(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getInt("CompletedSteps", 0);
    }

    public void setCompletedSteps(Context ctx, int value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putInt("CompletedSteps", value);
        mPrefsEditor.commit();
    }

    public String getRegisterType(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("registerType", "N");
    }

    public void setRegisterType(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("registerType", value);
        mPrefsEditor.commit();
    }

    public String getToken(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("usertoken", "");
    }

    public void setToken(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("usertoken", value);
        mPrefsEditor.commit();
    }

    public String getAuthToken(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("AuthToken", "");
    }

    public void setAuthToken(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("AuthToken", value);
        mPrefsEditor.commit();
    }

    public boolean isQuickboxSubsComplete(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("QuickboxSubsComplete", false);
    }


    public void setQuickboxSubsComplete(Context ctx, Boolean value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("QuickboxSubsComplete", value);
        mPrefsEditor.apply();
    }

    public boolean isAccountPrivate(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("AccountPrivate", false);
    }


    public void setAccountPrivate(Context ctx, Boolean value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("AccountPrivate", value);
        mPrefsEditor.apply();
    }

    public boolean isLastSeen(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("isLastSeen", false);
    }


    public void setLastSeen(Context ctx, Boolean value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("isLastSeen", value);
        mPrefsEditor.apply();
    }

    public boolean isNotificationReciept(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("NotificationReciept", false);
    }


    public void setNotificationReciept(Context ctx, Boolean value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("NotificationReciept", value);
        mPrefsEditor.apply();
    }

    public String getCountryId(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("countryid", "");
    }

    public void setCountryId(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("countryid", value);
        mPrefsEditor.commit();
    }

    public String getCountryImage(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("countryimage", "");
    }

    public void setCountryImage(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("countryimage", value);
        mPrefsEditor.commit();
    }

    public String getStateName(Context ctx) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("statename", "");
    }

    public void setStateName(Context ctx, String value) {
        SharedPreferences mPrefs;
        SharedPreferences.Editor mPrefsEditor;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("statename", value);
        mPrefsEditor.commit();
    }
}
