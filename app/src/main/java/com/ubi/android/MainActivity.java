package com.ubi.android;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.Fragments.AccountsFragment;
import com.ubi.android.Fragments.HomeFragment;
import com.ubi.android.Fragments.MessageFragment;
import com.ubi.android.Fragments.NotificationFragment;
import com.ubi.android.activity.AboutUSActivity;
import com.ubi.android.activity.ContactUSActivity;
import com.ubi.android.activity.HelpActivity;
import com.ubi.android.activity.LanguageActivity;
import com.ubi.android.activity.LocationsActivity;
import com.ubi.android.activity.MessageActivity;
import com.ubi.android.activity.MyAdsActivity;
import com.ubi.android.activity.MyFavouriteActivity;
import com.ubi.android.activity.MyLeadActivity;
import com.ubi.android.activity.NotificationActivity;
import com.ubi.android.activity.PostAdCategoryActivity;
import com.ubi.android.activity.PostRequiremntWelcomeActivity;
import com.ubi.android.models.Locations;
import com.ubi.android.models.LocationsResponse;
import com.ubi.android.models.MyFavouriteResponse;
import com.ubi.android.models.NotificationReponse;
import com.ubi.android.models.UserData;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    TextView usernametv, locationnametv;
    HomeFragment homeFragment;
    LocationManager locationManager;
    ArrayList<Locations> locations = new ArrayList<>();
    ImageView likes, notification, editprofile;
    LinearLayout locationlay, likenotilay;
    AccountsFragment accountsFragment;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        accountsFragment = new AccountsFragment();
        accountsFragment.setListner(listner);
        likes = findViewById(R.id.likes);
        editprofile = findViewById(R.id.editprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountsFragment.onEditClick();
            }
        });
        likenotilay = findViewById(R.id.likenotilay);
        locationlay = findViewById(R.id.locationlay);
        notification = findViewById(R.id.notification);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationnametv = findViewById(R.id.locationnametv);
        homeFragment = new HomeFragment();
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                locationlay.setVisibility(View.GONE);
                likenotilay.setVisibility(View.GONE);
                editprofile.setVisibility(View.GONE);
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, homeFragment).commit();
                        usernametv.setText(usernametext);
                        locationlay.setVisibility(View.VISIBLE);
                        likenotilay.setVisibility(View.VISIBLE);
                        break;
                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, new NotificationFragment()).commit();
                        usernametv.setText("Notification");
                        break;
                    case R.id.message:
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, new MessageFragment()).commit();
                        usernametv.setText("Messages");
                        break;
                    case R.id.Settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, accountsFragment).commit();
                        usernametv.setText("Account");
                        editprofile.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
        init();
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, homeFragment).commit();
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });
        findViewById(R.id.likes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyFavouriteActivity.class));
            }
        });
        findViewById(R.id.centerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostAdCategoryActivity.class);
                startActivityForResult(intent, 1001);
            }
        });
//        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//        startActivity(intent);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getLocation(params, false);
    }

    NavigationView mNavigationView;

    private void init() {
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ImageView hameburgicon = findViewById(R.id.hameburgicon);
        hameburgicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        mNavigationView = findViewById(R.id.mNavigationView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
        params.width = metrics.widthPixels;
        mNavigationView.setLayoutParams(params);

        View hearder = mNavigationView.getHeaderView(0);
        ImageView closebtn = hearder.findViewById(R.id.closebtn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                locationlay.setVisibility(View.VISIBLE);
                likenotilay.setVisibility(View.VISIBLE);
                editprofile.setVisibility(View.GONE);
                switch (item.getItemId()) {
                    case R.id.nav_help: {
                        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case R.id.nav_askseller:
                        Intent intent = new Intent(getApplicationContext(), PostRequiremntWelcomeActivity.class);
                        startActivityForResult(intent, 1002);
                        break;
                    case R.id.nav_account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottomnavigation, accountsFragment).commit();
                        usernametv.setText("Account");
                        editprofile.setVisibility(View.VISIBLE);
                        likenotilay.setVisibility(View.GONE);
                        break;
                    case R.id.nav_logout:
                        AppUtils.logoutdialog(MainActivity.this);
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, AboutUSActivity.class));
                        break;
                    case R.id.nav_country:
                        startActivity(new Intent(MainActivity.this, LocationsActivity.class));
                        break;
                    case R.id.nav_ads:
                        startActivity(new Intent(MainActivity.this, MyAdsActivity.class));
                        break;
                    case R.id.nav_language:
                        startActivity(new Intent(MainActivity.this, LanguageActivity.class));
                        break;
                    case R.id.nav_fav:
                        startActivity(new Intent(MainActivity.this, MyFavouriteActivity.class));
                        break;
                    case R.id.nav_msg:
                        startActivity(new Intent(MainActivity.this, MessageActivity.class));
                        break;
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity.this, ContactUSActivity.class));
                        break;
                    case R.id.nav_lead:
                        startActivity(new Intent(MainActivity.this, MyLeadActivity.class));
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        mNavigationView.getMenu().getItem(0).setActionView(R.layout.countryrightbtn);
        UserData data = AppPreferences.getInstance().getUserData(getApplicationContext());
        if (data != null) {
            usernametv = findViewById(R.id.usernametv);
            usernametext = "Hello " + data.getFirst_name();
            usernametv.setText(usernametext);
            View view = mNavigationView.getHeaderView(0);
            TextView username = view.findViewById(R.id.username);
            username.setText(data.getFirst_name() + " " + data.getLast_name());
            TextView mobile = view.findViewById(R.id.mobile);
            mobile.setText(data.getPhone());
            TextView emailtv = view.findViewById(R.id.emailtv);
            emailtv.setText(data.getEmail());
            CircleImageView profile_image = view.findViewById(R.id.profile_image);
            if (!TextUtils.isEmpty(data.getProfile_img())) {
                RequestOptions myOptions = new RequestOptions()
                        .override(300, 300).centerCrop();
                Glide.with(this).load(data.getProfile_img()).apply(myOptions).into(profile_image);
            }
        }

    }

    String usernametext = "";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (homeFragment != null)
            homeFragment.onActivityResultV2(requestCode, resultCode, data);
        if (accountsFragment != null)
            accountsFragment.onActivityResultV2(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!TextUtils.isEmpty(AppPreferences.getInstance().getStateName(getApplicationContext()))) {
//                locationnametv.setText(AppPreferences.getInstance().getStateName(getApplicationContext()));
                String icon = AppPreferences.getInstance().getCountryImage(getApplicationContext());
                Log.d(MainActivity.class.getName(), icon);
                if (!TextUtils.isEmpty(icon)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(icon).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    try {
                                        if (bitmap != null) {
                                            Drawable d = new BitmapDrawable(getResources(), bitmap);
                                            mNavigationView.getMenu().getItem(0).setIcon(d);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                    }, 4000);
                }
            } else {

            }

            if (locations.size() == 0) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
                params.put("limit", "1000");
                getLocation(params, true);

            } else
                fecthfusedlocation();

            Map<String, String> params = new HashMap<String, String>();
            params.put("page", "1");
            params.put("limit", "1000");
            getFavCount(params);
            getNotificationCount(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void fetchLocation() {
//        if (checkLocationPermission()) {
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_FINE);
//            criteria.setPowerRequirement(Criteria.POWER_HIGH);
//            criteria.setAltitudeRequired(false);
//            criteria.setSpeedRequired(false);
//            criteria.setCostAllowed(true);
//            criteria.setBearingRequired(false);
//            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//            locationManager.requestSingleUpdate(criteria, new LocationListener() {
//                @Override
//                public void onLocationChanged(@NonNull Location location) {
//                    Log.d(LocationsActivity.class.getName(), location.toString());
//                    if (location != null) {
//                        getAddress(location.getLatitude(), location.getLongitude());
//                    }
//                }
//            }, Looper.myLooper());
//        }
//    }

    private void fecthfusedlocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
                return;
            }
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                getAddress(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });

        } catch (Exception unlikely) {
            FirebaseCrashlytics.getInstance().recordException(unlikely);
            Log.d(LocationsActivity.class.getName(), "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location permission required")
                        .setMessage(getResources().getString(R.string.app_name) + " need your location permission to detect your accurate location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        101);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        101);
            }
            return false;
        } else {
            return true;
        }
    }

    int retrycount = 0;
    int RETRY_THRESHOLD = 3;

    public void getAddress(double lat, double lng) {
        if (AppUtils.isConnectingToInternet(getApplicationContext())) {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address obj = addresses.get(0);
                    String add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getCountryName();
                    add = add + "\n" + obj.getCountryCode();
                    add = add + "\n" + obj.getAdminArea();
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();

                    Log.v(LocationsActivity.class.getName(), add);
                    String address = obj.getSubLocality() + ", " + obj.getCountryName();
                    locationnametv.setText(address);
                    String Country_id = "", icon = "";
                    Country_id = getCountry_id(obj.getCountryName());
                    icon = getCountry_icon(obj.getCountryName());
                    AppPreferences.getInstance().setCountryId(getApplicationContext(), Country_id);
                    AppPreferences.getInstance().setCountryImage(getApplicationContext(), icon);
                    AppPreferences.getInstance().setStateName(getApplicationContext(), address);

                    if (!TextUtils.isEmpty(icon)) {
                        Picasso.get().load(icon).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    if (bitmap != null) {
                                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                                        mNavigationView.getMenu().getItem(0).setIcon(d);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                } else {
                    new FetchLocationfromAPI(lat, lng).execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
                AppUtils.showalert(MainActivity.this, e.getMessage(), false);
                if (retrycount < RETRY_THRESHOLD) {
                    retrycount++;
                    getAddress(lat, lng);
                }
            }
        }
    }

    private String getCountry_id(String countryname) {
        String countryid = "";
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).country_name.equalsIgnoreCase(countryname)) {
                countryid = locations.get(i).country_id;
            }
        }
        return countryid;
    }

    private String getCountry_icon(String countryname) {
        String icon = "";
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).country_name.equalsIgnoreCase(countryname)) {
                icon = locations.get(i).icon;
            }
        }
        return icon;
    }

    private void getLocation(Map<String, String> params, boolean fetchlocation) {
        if (AppUtils.isConnectingToInternet(this)) {
            locations.clear();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<LocationsResponse> call = apiInterface.countryList(token, params);
            call.enqueue(new Callback<LocationsResponse>() {
                @Override
                public void onResponse(Call<LocationsResponse> call, Response<LocationsResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        locations.addAll(response.body().payload);
                                        if (fetchlocation)
                                            fecthfusedlocation();
                                    }
                                }
                            }
                        } else {
                            AppUtils.showalert(MainActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LocationsResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                }
            });
        } else {
            AppUtils.Nointernetalert(MainActivity.this);
        }
    }

    private void getFavCount(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<MyFavouriteResponse> call = apiInterface.myFavorite(token, params);
            call.enqueue(new Callback<MyFavouriteResponse>() {
                @Override
                public void onResponse(Call<MyFavouriteResponse> call, Response<MyFavouriteResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    int count = response.body().dev_message.unread_count;
                                    if (count > 0) {
                                        likes.setImageResource(R.drawable.ic_favourite_available);
                                    } else {
                                        likes.setImageResource(R.drawable.ic_hometopheart);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MyFavouriteResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                }
            });
        }
    }

    private void getNotificationCount(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<NotificationReponse> call = apiInterface.getNotificationLog(token, params);
            call.enqueue(new Callback<NotificationReponse>() {
                @Override
                public void onResponse(Call<NotificationReponse> call, Response<NotificationReponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    int count = response.body().dev_message.unread_count;
                                    if (count > 0) {
                                        notification.setImageResource(R.drawable.ic_notification_available);
                                    } else {
                                        notification.setImageResource(R.drawable.ic_hometopnotification);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<NotificationReponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                }
            });
        }
    }

    onClickAccounts listner = new onClickAccounts() {
        @Override
        public void onClickDatepicker() {
            com.ubi.android.utils.DatePicker picker = new com.ubi.android.utils.DatePicker();
            picker.show(getSupportFragmentManager(), "datepicker");
        }
    };

    public interface onClickAccounts {
        void onClickDatepicker();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat apipostFormater = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate = postFormater.format(mCalendar.getTime());
        String apidate = apipostFormater.format(mCalendar.getTime());
        if (accountsFragment != null)
            accountsFragment.onDobSelected(apidate, selectedDate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        fecthfusedlocation();
                    }

                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Location permission denied")
                            .setMessage("Unable to detect your accurate location")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create()
                            .show();
                }
                return;
            }

        }
    }

    public String getStringFromLocation(double lat, double lng)
            throws Exception {
        String stradd = "";
        String address = String
                .format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&key=" + AppUtils.LOC_KEY, lat, lng);
        Log.d(LocationsActivity.class.getName(), address);
        URL url = new URL(address);
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = (HttpURLConnection) url
                .openConnection();
        InputStream in = urlConnection.getInputStream();
        InputStreamReader isw = new InputStreamReader(in);
        int data = isw.read();
        while (data != -1) {
            char current = (char) data;
            data = isw.read();
            stringBuilder.append((char) current);
        }

        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        Log.d(LocationsActivity.class.getName(), stringBuilder.toString());

        if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
            String adminarea = "", country = "";
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                JSONArray address_components = result.getJSONArray("address_components");
                for (int j = 0; j < address_components.length(); j++) {
                    JSONObject addresult = address_components.getJSONObject(j);
                    JSONArray types = addresult.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        String type = types.getString(k);
                        if (type.equalsIgnoreCase("sublocality_level_1")) {
                            adminarea = addresult.getString("long_name");

                        } else if (type.equalsIgnoreCase("country")) {
                            country = addresult.getString("long_name");
                        }
                    }
                }
                stradd = adminarea + ", " + country;
                String Country_id = "", icon = "";
                Country_id = getCountry_id(country);
                icon = getCountry_icon(country);
                AppPreferences.getInstance().setCountryId(getApplicationContext(), Country_id);
                AppPreferences.getInstance().setCountryImage(getApplicationContext(), icon);
                AppPreferences.getInstance().setStateName(getApplicationContext(), adminarea + ", " + country);

            }
        }
        return stradd;
    }

    class FetchLocationfromAPI extends AsyncTask<Void, Void, String> {
        double lat;
        double lng;

        public FetchLocationfromAPI(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return getStringFromLocation(lat, lng);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            if (!TextUtils.isEmpty(unused))
                locationnametv.setText(unused);
        }
    }
}