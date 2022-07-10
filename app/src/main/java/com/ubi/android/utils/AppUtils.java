package com.ubi.android.utils;


import static android.content.Context.WIFI_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.ubi.android.R;
import com.ubi.android.activity.LoginActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    //Test
    public static final String Public_Key = "FLWPUBK_TEST-a2d398c688f135e2682911ccc70074ce-X";
    public static final String Encryption_Key = "FLWSECK_TESTb16e2fd0d30d";

    //Live
//    public static final String Public_Key = "FLWPUBK-5d6653b8ac41ee7f3663d8c17247016c-X";
//    public static final String Encryption_Key = "0267f165765c867d8d11cd45";
    public static final String LOC_KEY = "AIzaSyAiEVAO6i4dkTPg0vT8tZHf8wqnCg8LfYM";
    public static final String KEY = "jJsG-XnDzT@vSkJz";
    public static final String STORE_ID = "25168";
    public static String GCMPREFERENCES = "gcm";
    public static String LoginPREFERENCES = "UserLogin";
    public static String GlobalPREFERENCES = "global";
    public static String PREFERENCE = "prefence";
    //    public static String BASE_URL = "http://3.111.120.176/api/v1/";//
    public static String BASE_URL = "http://www.exibine.com/api/v1/";//

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static Boolean isValidInteger(String value) {
        try {
            Integer val = Integer.valueOf(value);
            if (val != null)
                return true;
            else
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getFileName(Activity activity, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getIpaddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public static String parseDate(String date) {
        String formatdate = "";
        String str[] = date.split("T");
        if (str.length > 0) {
            formatdate = str[0];
            formatdate = getFormateddate(formatdate, "yyyy-mm-dd");
        }
        return formatdate;
    }

    public static void disableEdittext(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    public static String getFormateddate(String dateString, String currentFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
        try {
//            Date dates = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(dateString.replaceAll("Z$", "+0000"));


            Date date = sdf.parse(dateString);
            return DateFormat.getDateInstance().format(date);
//            String formated = new SimpleDateFormat(format).format(date);
//            return formated;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTime(String time) {
        String str[] = time.split("-");
        if (str != null && str.length > 0)
            return str[0];
        return "";
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    static public String getDateStr(long milliseconds, Date currentDate) {
        // seconds diff
        long diff = (long) (currentDate.getTime() - milliseconds) / 1000;
        if (diff < 3600) {
            long minutesAgo = (long) diff / 60;
            if (minutesAgo < 1)
                return minutesAgo + "sec";
            else if (minutesAgo == 1)
                return "1min ";
            else
                return (String.valueOf(minutesAgo) + "min");
        } else if (diff < 7200)
            return "1hr";
        else if (diff < 86400)
            return (String.valueOf((int) diff / 3600) + "hr");
        else {
            Date d = new Date(milliseconds);
            int date_diff = 0;
//            Log.d("", "current date " + (currentDate.getMonth() + 1) + " feed month " + (d.getMonth() + 1) + " feed date " + d.getDate());
            if (d.getMonth() + 1 == currentDate.getMonth() + 1) {
                date_diff = Math.abs(currentDate.getDate() - d.getDate());
                Log.d("", "current date diff " + date_diff);
                return String.valueOf(date_diff + "d");

            } else if (currentDate.getMonth() + 1 > d.getMonth() + 1) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, 2016);
                calendar.set(Calendar.MONTH, d.getMonth());
                int numDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


//                Log.d("", "current maxdays " + numDays);
                int elaspeddays = Math.abs(numDays - d.getDate());
                elaspeddays = elaspeddays + currentDate.getDate();
//                Log.d("", "current elaspeddays " + elaspeddays);
                return String.valueOf(elaspeddays + "d");

            }

            return String.valueOf(Math.abs(currentDate.getDate() - d.getDate()) + "d");
        }
    }

    public static boolean isValidUrl(String url) {

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if (m.matches())
            return true;
        else
            return false;
    }

    public static String getFileNameFromUrl(String url) {


        return url.substring(url.lastIndexOf('/') + 1);
    }

    public static String getElapsedDaysText(Calendar c1, Calendar c2) {
        String elapsedDaysText = null;
        try {
            long milliSeconds1 = c1.getTimeInMillis();
            long milliSeconds2 = c2.getTimeInMillis();
            long periodSeconds = (milliSeconds2 - milliSeconds1) / 1000;
            long elapsedDays = periodSeconds / 60 / 60 / 24;
            Calendar c = Calendar.getInstance();
            int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (elapsedDays > 31) {
//
                int months = (int) elapsedDays / 31;
                elapsedDays = elapsedDays - (long) monthMaxDays;
                elapsedDaysText = String.valueOf(months + "m " + elapsedDays + "d");
            } else {
                elapsedDaysText = String.format("%d d", elapsedDays);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elapsedDaysText;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;

    }

//    public static boolean checkPlayServices(Activity activity) {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(activity);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 10)
//                        .show();
//            } else {
//                activity.finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public static boolean isEditTextContainEmail(TextInputEditText argEditText) {

        try {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(argEditText.getText());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEditTextContainEmail(EditText argEditText) {

        try {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(argEditText.getText());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getdate() {
        String myFormat = "dd-MM-yyyy";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return (sdf.format(calendar.getTime()));
    }

    public static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    public static void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
//        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

//    public static void showAlert(final Activity act, String title, String subtitle) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(act);
//        LayoutInflater inflater = act.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
//        dialogBuilder.setView(dialogView);
//        TextView titletv = dialogView.findViewById(R.id.title);
//        titletv.setText(title);
//
//        TextView subtitletv = dialogView.findViewById(R.id.subtitile);
//        subtitletv.setText(subtitle);
//        if (TextUtils.isEmpty(subtitle))
//            subtitletv.setVisibility(View.GONE);
//
//        CardView btnok = dialogView.findViewById(R.id.btnok);
//        final AlertDialog alertDialog = dialogBuilder.create();
//        btnok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (alertDialog != null)
//                    alertDialog.dismiss();
//            }
//        });
//        alertDialog.show();
//    }


    public static void showalert(final Activity act, String msg, final boolean closeactivity) {
        new AlertDialog.Builder(act)
                .setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (closeactivity)
                            act.finish();

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void Nointernetalert(final Activity act) {
        new AlertDialog.Builder(act)
                .setMessage("No internet connection, Please Connect to internet.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void Nointernetalert(final Activity act, final boolean close) {
        new AlertDialog.Builder(act)
                .setMessage("No internet connection, Please Connect to internet.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (close)
                            act.finish();

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

//    public static HttpParams setTimeout() {
//        HttpParams httpParameters = new BasicHttpParams();
//        // Set the timeout in milliseconds until a connection is established.
//        int timeoutConnection = 15000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters,
//                timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 15000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//        return httpParameters;
//    }

    public static void hidekeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void logoutdialog(final Activity act) {
        new AlertDialog.Builder(act)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        clearcaches(act);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static String getCurrentFormattedDate() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd MMM");
        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public static void clearcaches(final Activity context) {
        SharedPreferences sharedpreferences = context
                .getSharedPreferences(
                        AppUtils.LoginPREFERENCES,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();


        SharedPreferences gcmsharedpreferences = context.getSharedPreferences(
                AppUtils.GCMPREFERENCES, Context.MODE_PRIVATE);


        SharedPreferences.Editor editors = gcmsharedpreferences.edit();
        editors.clear();
        editors.commit();

        AppPreferences.getInstance().Logout(context);

        Intent i = new Intent(context, LoginActivity.class);
        i.putExtra("fromhome", false);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        context.finish();
    }

    public static boolean getIsLanguageSelect(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        boolean userId = sharedpreferences.getBoolean("islangselect", false);
        Log.d("", "userId " + userId);
        return userId;
    }

    public static void setIsLanguageSelect(Context context, boolean status) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("islangselect", status).commit();
    }

    public static void setcountryid(Context context, String countryid) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("countryid", countryid).commit();

    }

    public static String getcountryid(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        String countryid = sharedpreferences.getString("countryid", "");
        Log.d("", "access_token " + countryid);
        return countryid;
    }

    public static void setlanguageid(Context context, String countryid) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("languageid", countryid).commit();

    }

    public static String getlanguageid(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        String countryid = sharedpreferences.getString("countryid", "");
        Log.d("", "access_token " + countryid);
        return countryid;
    }

    public static void setculture_id(Context context, String countryid) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("culture_id", countryid).commit();

    }

    public static String getculture_id(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        String countryid = sharedpreferences.getString("culture_id", "");

        return countryid;
    }

    public static boolean isIntroSeen(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.GlobalPREFERENCES, Context.MODE_PRIVATE);
        boolean BranchId = sharedpreferences.getBoolean("IntroSeen", false);
        return BranchId;
    }

    public static void setIntroSeen(Context context, boolean isseen) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.GlobalPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("IntroSeen", isseen).commit();
    }

    public static String getData(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.LoginPREFERENCES, Context.MODE_PRIVATE);
        String data = sharedpreferences.getString(key, "");

        return data;
    }

    public static void setImeiNo(Context context, String imeival) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.LoginPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("im", imeival).commit();
    }


    //====================================




/*

    public static void setPollId(Context context,String poll) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("pollid", poll).commit();

    }

    public static String getPollId(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.PREFERENCE, Context.MODE_PRIVATE);
        String polll = sharedpreferences.getString("pollid", "");

        return polll;
    }






    //=== culture id =====

    public static void setCultureId(Context context, String key, String data) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.LoginPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, data).commit();
    }

    public static String getCultureId(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.LoginPREFERENCES, Context.MODE_PRIVATE);
        String data = sharedpreferences.getString(key, "");

        return data;
    }
*/


    //====================================

    public static String getImeiNo(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(
                AppUtils.LoginPREFERENCES, Context.MODE_PRIVATE);
        String data = sharedpreferences.getString("im", "");

        return data;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);


            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }


    //====================================

    public static Date getDate(long milliSeconds, String dateFormat) {

        DateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dtStart = formatter.format(calendar.getTime());
        Date date = null;
        try {
            date = format.parse(dtStart);
            System.out.println(date.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static int daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        int daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    static File getStoreFile(final String title) {
        File internalFilesDir = new File(Environment.getExternalStorageDirectory(), "Shopwati");
        if (!internalFilesDir.exists()) {
            internalFilesDir.mkdir();
        }
        final File mediaDataDir = new File(internalFilesDir, title);

        return mediaDataDir;
    }

//    public static void toast(Activity activity, String Message) {
//        Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), Message, Snackbar.LENGTH_LONG);
//        ViewGroup group = (ViewGroup) snack.getView();
//        group.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
//        snack.show();
//    }

    public static Typeface gettypeface(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "AvenirLTStd-Medium.otf");
        return face;
    }

    private static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image.png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
                                                     int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static void deleteDirectoryTree(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteDirectoryTree(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static Bitmap doParse(Bitmap tempBitmap, int w, int h) {
        // byte[] data = response.data;


        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;

        // If we have to resize this image, first get the natural bounds.
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(response, decodeOptions);
        // BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(w, h, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(w, h, actualHeight, actualWidth);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't
        // support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                actualHeight, desiredWidth, desiredHeight);
        // Bitmap tempBitmap = BitmapFactory.decodeFile(response, decodeOptions);

        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null
                && (tempBitmap.getWidth() > desiredWidth || tempBitmap
                .getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth,
                    desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }

        return bitmap;
    }

    private static int findBestSampleSize(int actualWidth, int actualHeight,
                                          int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    public static void SaveImage(Context context, Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Shopwati");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "shopwati" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String data, Context context) {
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "PrabhutiSys");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String name = "config_" + System.currentTimeMillis() + "_.txt";
            File file = new File(dir, name);
            file.createNewFile();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static AlertDialog Progress(Activity act) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_progress, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        return alertDialog;

    }

    public static boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static Typeface getLatoRegular(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat-Regular.ttf");
        return face;
    }

    public static Typeface getLatoBold(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat-SemiBold.ttf");
        return face;
    }

    public static String getCapsSentences(String tagName) {
        String[] splits = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            String eachWord = splits[i];
            if (i > 0 && eachWord.length() > 0) {
                sb.append(" ");
            }
            String cap = eachWord.substring(0, 1).toUpperCase()
                    + eachWord.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        if (TextUtils.isEmpty(vId))
            vId = getVideoIdFromYoutubeUrl(ytUrl);
        return vId;
    }

    static String getVideoIdFromYoutubeUrl(String url) {
        String videoId = null;
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    public static String convertDate(String dateInMilliseconds) {
        Date date = new Date();
        if (!TextUtils.isEmpty(dateInMilliseconds))
            date.setTime(Long.parseLong(dateInMilliseconds));
        return printDifference(date, new Date());
//        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
//        return format.format(date.getTime());
    }

    private static String printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        if (elapsedDays > 0) {
            return elapsedDays + " day ago";
        } else if (elapsedHours > 0) {
            return elapsedHours + " hour ago";
        } else if (elapsedMinutes > 0) {
            return elapsedMinutes + " min ago";
        } else {
            return elapsedSeconds + " sec ago";
        }
    }

    public static void clearcache(final Activity context) {
        SharedPreferences sharedpreferences = context
                .getSharedPreferences(
                        AppUtils.LoginPREFERENCES,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();


        SharedPreferences gcmsharedpreferences = context.getSharedPreferences(
                AppUtils.GCMPREFERENCES, Context.MODE_PRIVATE);


        SharedPreferences.Editor editors = gcmsharedpreferences.edit();
        editors.clear();
        editors.commit();

        AppPreferences.getInstance().Logout(context);

        Intent i = new Intent(context, LoginActivity.class);
        i.putExtra("fromhome", false);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        context.finish();
    }
}

