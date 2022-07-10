package com.ubi.android.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.picasso.Picasso;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.R;
import com.ubi.android.adapters.LocationAdapter;
import com.ubi.android.adapters.LocationStateAdapter;
import com.ubi.android.interfaces.OnAdapterItemClickListner;
import com.ubi.android.models.Locations;
import com.ubi.android.models.LocationsResponse;
import com.ubi.android.models.StateResponse;
import com.ubi.android.models.StatesModel;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationsActivity extends AppCompatActivity {
    final int REQUEST_CODE_SPEECH_INPUT = 101;
    TextView locationtv;
    RecyclerView recyclerview;
    ProgressBar progressbar;
    ArrayList<Locations> locations = new ArrayList<>();
    ArrayList<StatesModel> statesModels = new ArrayList<>();
    LocationAdapter adapter;
    TextView nodata;
    protected LocationManager locationManager;
    LinearLayout detectmylocation;
    TextView countrytxt;
    LinearLayout selectedcountrylay;
    ImageView countryimg;
    TextView country;
    LocationStateAdapter locationStateAdapter;
    ProgressDialog pd;
    boolean isedit;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        isedit = getIntent().getBooleanExtra("isedit", true);
        init();

    }

    String country_id, icon, countryname;

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        TextView searchet = findViewById(R.id.searchet);
        searchet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LocationsActivity.this, SearchActivity.class));
            }
        });
        pd = new ProgressDialog(LocationsActivity.this);
        pd.setMessage("Fetching Location, Please wait...");
        pd.setCanceledOnTouchOutside(false);
        country = findViewById(R.id.country);
        countryimg = findViewById(R.id.countryimg);
        countrytxt = findViewById(R.id.countrytxt);
        selectedcountrylay = findViewById(R.id.selectedcountrylay);
        selectedcountrylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedcountrylay.setVisibility(View.GONE);
                countrytxt.setVisibility(View.VISIBLE);
                recyclerview.setAdapter(adapter);
            }
        });
        detectmylocation = findViewById(R.id.detectmylocation);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        detectmylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationsActivity.this);
                    pd.show();
//                    Criteria criteria = new Criteria();
//                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                    criteria.setPowerRequirement(Criteria.POWER_HIGH);
//                    criteria.setAltitudeRequired(false);
//                    criteria.setSpeedRequired(false);
//                    criteria.setCostAllowed(true);
//                    criteria.setBearingRequired(false);
//                    criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//                    criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//
//                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(@NonNull Location location) {
//                            Log.d(LocationsActivity.class.getName(), location.toString());
//                            if (location != null) {
//                                pd.setMessage("Fetching address, Please wait...");
////                                Toast.makeText(getApplicationContext(), "Current location found, Fetching address", Toast.LENGTH_LONG).show();
//                                getAddress(location.getLatitude(), location.getLongitude());
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Unable to found location", Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onProviderDisabled(@NonNull String provider) {
//                            LocationListener.super.onProviderDisabled(provider);
//                            Toast.makeText(getApplicationContext(), "Provider has disabled", Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {
//                            LocationListener.super.onStatusChanged(provider, status, extras);
//                            Toast.makeText(getApplicationContext(), "Provider has changed to " + provider, Toast.LENGTH_LONG).show();
//                        }
//                    }, Looper.myLooper());
                    fecthfusedlocation();

                }
            }
        });

        locationtv = findViewById(R.id.locationtv);
        nodata = findViewById(R.id.nodata);
        adapter = new LocationAdapter(this, locations);
        adapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                Locations location = locations.get(pos);
                if (!TextUtils.isEmpty(location.icon)) {
                    Picasso.get().load(location.icon).into(countryimg);
                }
                countryname = location.country_name;
                country_id = location.country_id;
                icon = location.icon;
                country.setText(location.country_name);
                selectedcountrylay.setVisibility(View.VISIBLE);
                countrytxt.setVisibility(View.GONE);
                recyclerview.setAdapter(locationStateAdapter);
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
                params.put("limit", "1000");
                params.put("country_id", location.country_id);
                getStates(params);
            }
        });

        locationStateAdapter = new LocationStateAdapter(this, statesModels);
        locationStateAdapter.setListner(new OnAdapterItemClickListner() {
            @Override
            public void onClick(View view, int pos) {
                if (isedit) {
                    AppPreferences.getInstance().setCountryId(getApplicationContext(), country_id);
                    AppPreferences.getInstance().setCountryImage(getApplicationContext(), icon);
                    AppPreferences.getInstance().setStateName(getApplicationContext(), statesModels.get(pos).state_name + ", " + countryname);
                    Toast.makeText(getApplicationContext(), "Location selected " + statesModels.get(pos).state_name, Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra("location", statesModels.get(pos).state_name + ", " + countryname);
                intent.putExtra("state", statesModels.get(pos).state_name);
                intent.putExtra("counrty", countryname);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressbar = findViewById(R.id.progressbar);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview.setAdapter(adapter);
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", "1");
        params.put("limit", "1000");
        getProductDetails(params);

        RelativeLayout miclay = findViewById(R.id.miclay);
        miclay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search by speak");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void getProductDetails(Map<String, String> params) {
        if (AppUtils.isConnectingToInternet(this)) {
            locations.clear();
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<LocationsResponse> call = apiInterface.countryList(token, params);
            call.enqueue(new Callback<LocationsResponse>() {
                @Override
                public void onResponse(Call<LocationsResponse> call, Response<LocationsResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        locations.addAll(response.body().payload);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                nodata.setText(response.body().message);
                                nodata.setVisibility(View.VISIBLE);
//                                AppUtils.showalert(SubCategoryDetailActivity.this, response.body().message, false);
                            }
                        } else {
                            AppUtils.showalert(LocationsActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LocationsResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(LocationsActivity.this);
        }
    }

    private void getStates(Map<String, String> params) {
        statesModels.clear();
        locationStateAdapter.notifyDataSetChanged();
        if (AppUtils.isConnectingToInternet(this)) {
            progressbar.setVisibility(View.VISIBLE);
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(this);
            Call<StateResponse> call = apiInterface.stateList(token, params);
            call.enqueue(new Callback<StateResponse>() {
                @Override
                public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                    Log.d("TAG", response.code() + "");
                    try {
                        progressbar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body().code == 1) {
                                if (response.body().payload != null) {
                                    if (response.body().payload != null) {
                                        statesModels.addAll(response.body().payload);
                                        locationStateAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                nodata.setText(response.body().message);
                                nodata.setVisibility(View.VISIBLE);
                            }
                        } else {
                            AppUtils.showalert(LocationsActivity.this, response.message(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<StateResponse> call, Throwable t) {
                    t.printStackTrace();
                    call.cancel();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            AppUtils.Nointernetalert(LocationsActivity.this);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d(LocationsActivity.class.getName(), "onPointerCaptureChanged" + hasCapture);
        pd.dismiss();
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(LocationsActivity.this, Locale.getDefault());
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
                locationtv.setText(address);
                String Country_id = "", icon = "";
                Country_id = getCountry_id(obj.getCountryName());
                icon = getCountry_icon(obj.getCountryName());
                AppPreferences.getInstance().setCountryId(getApplicationContext(), Country_id);
                AppPreferences.getInstance().setCountryImage(getApplicationContext(), icon);
                AppPreferences.getInstance().setStateName(getApplicationContext(), address);
                pd.dismiss();
                if (!isedit) {
                    Intent intent = new Intent();
                    intent.putExtra("location", address);
                    intent.putExtra("state", obj.getAdminArea());
                    intent.putExtra("counrty", obj.getCountryName());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } else {
                new FetchLocationfromAPI(lat, lng).execute();
            }
        } catch (Exception e) {
            pd.dismiss();
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            new FetchLocationfromAPI(lat, lng).execute();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                ActivityCompat.requestPermissions(LocationsActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
//                searchet.setText(
//                        Objects.requireNonNull(result).get(0));
                    Intent intent = new Intent(LocationsActivity.this, SearchActivity.class);
                    intent.putExtra("str", Objects.requireNonNull(result).get(0));
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            AppUtils.showalert(LocationsActivity.this, "Something went wrong", false);
        }
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
            pd.setMessage("Fetching current location");
            pd.show();
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
            pd.dismiss();
            if (!TextUtils.isEmpty(unused))
                locationtv.setText(unused);
        }
    }

    public String getStringFromLocation(double lat, double lng)
            throws Exception {
        String stradd = "";
        String address = String
                .format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&key="+AppUtils.LOC_KEY, lat, lng);
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
                if (!isedit) {
                    Intent intent = new Intent();
                    intent.putExtra("location", adminarea + " " + country);
                    intent.putExtra("state", adminarea);
                    intent.putExtra("counrty", country);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
        return stradd;
    }

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
                                pd.setMessage("Fetching address, Please wait...");
                                Toast.makeText(getApplicationContext(), "Current location found, Fetching address", Toast.LENGTH_LONG).show();
                                getAddress(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });

        } catch (Exception unlikely) {
            FirebaseCrashlytics.getInstance().recordException(unlikely);
            Log.d(LocationsActivity.class.getName(), "Lost location permission. Could not request updates. " + unlikely);
        }
    }
}