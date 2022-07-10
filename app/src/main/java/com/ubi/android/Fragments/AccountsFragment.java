package com.ubi.android.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ubi.android.API.APIClient;
import com.ubi.android.API.APIInterface;
import com.ubi.android.MainActivity;
import com.ubi.android.R;
import com.ubi.android.activity.PostAdSecondStep;
import com.ubi.android.adapters.GenderSpinnerAdapter;
import com.ubi.android.models.BaseResponse;
import com.ubi.android.models.UserData;
import com.ubi.android.models.UserResponse;
import com.ubi.android.utils.AppPreferences;
import com.ubi.android.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountsFragment extends Fragment {

    EditText firstnametv, lastnametv, mobiletv, emailtv, citytv, companyname, companyvideo, about, education;
    TextView dob;
    Button submitbtntv;
    public AlertDialog pd;
    Spinner genderspinner;
    String[] gender = {"Select Gender", "Male", "Female", "Other"};
    String genderstr = "";
    CircleImageView profile_image;
    String profile_img;
    MainActivity.onClickAccounts listner;
    boolean isediting = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.accountfragment, container, false);
        pd = AppUtils.Progress(getActivity());
        init(v);
        return v;
    }

    public void setListner(MainActivity.onClickAccounts listner) {
        this.listner = listner;
    }

    public void onEditClick() {
        isediting = true;
        enabledisableviews(true);
        firstnametv.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(firstnametv, InputMethodManager.SHOW_IMPLICIT);
        submitbtntv.setVisibility(View.VISIBLE);
    }

    public void onDobSelected(String apidate, String date) {
        dobstr = apidate;
        dob.setText(date);
    }

    private void enabledisableviews(boolean isenable) {
        firstnametv.setEnabled(isenable);
        lastnametv.setEnabled(isenable);
        mobiletv.setEnabled(isenable);
        emailtv.setEnabled(isenable);
        citytv.setEnabled(isenable);
        companyname.setEnabled(isenable);
        companyvideo.setEnabled(isenable);
        about.setEnabled(isenable);
        education.setEnabled(isenable);
        dob.setClickable(isenable);
        genderspinner.setEnabled(isenable);
    }

    private void init(View v) {
        profile_image = v.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isediting)
                    showAlert(getActivity());
            }
        });
        genderspinner = v.findViewById(R.id.genderspinner);
        genderspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    genderstr = gender[i].equalsIgnoreCase("Male") ? "M" : (gender[i].equalsIgnoreCase("Female") ? "F" : "O");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        GenderSpinnerAdapter aa = new GenderSpinnerAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList(gender)
        );
//        GenderSpinnerAdapter aa = new GenderSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, gender);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderspinner.setAdapter(aa);
        about = v.findViewById(R.id.about);
        dob = v.findViewById(R.id.dob);
        education = v.findViewById(R.id.education);
        firstnametv = v.findViewById(R.id.firstnametv);
        lastnametv = v.findViewById(R.id.lastnametv);
        mobiletv = v.findViewById(R.id.mobiletv);
        emailtv = v.findViewById(R.id.emailtv);
        citytv = v.findViewById(R.id.citytv);
        companyname = v.findViewById(R.id.companyname);
        companyvideo = v.findViewById(R.id.companyvideo);
        submitbtntv = v.findViewById(R.id.submitbtntv);
        submitbtntv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstnametv.getText().toString();
                String lastname = lastnametv.getText().toString();
                String mobile = mobiletv.getText().toString();
                String email = emailtv.getText().toString();
                String city = citytv.getText().toString();
                String companynamestr = companyname.getText().toString();
                String companyvideostr = companyvideo.getText().toString();
                String aboutstr = about.getText().toString();
//                String dobstr = dob.getText().toString();
                String educationstr = education.getText().toString();

                if (TextUtils.isEmpty(firstname)) {
                    AppUtils.showalert(getActivity(), "Please enter first name", false);
                } else if (TextUtils.isEmpty(lastname)) {
                    AppUtils.showalert(getActivity(), "Please enter last name", false);
                } else if (TextUtils.isEmpty(mobile)) {
                    AppUtils.showalert(getActivity(), "Please enter mobile number", false);
                } else if (TextUtils.isEmpty(email)) {
                    AppUtils.showalert(getActivity(), "Please enter email", false);
                } else if (!AppUtils.isEditTextContainEmail(emailtv)) {
                    AppUtils.showalert(getActivity(), "Please enter valid email", false);
                } else if (TextUtils.isEmpty(city)) {
                    AppUtils.showalert(getActivity(), "Please enter city", false);
                } else if (TextUtils.isEmpty(aboutstr)) {
                    AppUtils.showalert(getActivity(), "Please enter about", false);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name", firstname);
                    params.put("last_name", lastname);
                    params.put("phone", mobile);
                    params.put("city", city);
                    params.put("email", email);
                    params.put("about", aboutstr);
                    params.put("user_type", "1");
                    params.put("profile_img", profile_img);
                    params.put("company_video", companyvideostr);
                    params.put("company_name", companynamestr);
                    params.put("gender", genderstr);
                    params.put("birth_date", dobstr);
                    params.put("education", educationstr);
                    register(params, mobile);
                }
            }
        });
        enabledisableviews(false);
        myProfile();
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listner != null && isediting)
                    listner.onClickDatepicker();
            }
        });
    }

    private void register(Map<String, String> params, String phone) {
        AppUtils.hidekeyboard(getActivity(), emailtv);
        if (AppUtils.isConnectingToInternet(getActivity())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<BaseResponse> call = apiInterface.updateProfile(token, params);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            isediting = false;
                            Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                            enabledisableviews(false);
                            submitbtntv.setVisibility(View.GONE);
                        } else {
                            AppUtils.showalert(getActivity(), response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(getActivity(), response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    call.cancel();
                    t.printStackTrace();
                    AppUtils.showalert(getActivity(), t.getMessage(), false);
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }

    private void takePictureFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, 105);
            }
        }
    }

    // check permision when we try to take picture from camera

    private boolean checkPermisionRequest() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera();

        } else {
            /// To check third case
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                Log.e("TAG", "onRequestPermissionsResult: " + "");
                Toast.makeText(getActivity(), "Permission permanent denied", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Here take photo through galary

    private void takePictureFromGalary() {
        Intent takePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(takePhoto, 1);
    }

    public void onActivityResultV2(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
        if (requestCode == 2296) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {
                    Toast.makeText(getActivity(), "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        CropImage.activity(selectedImage)
                                .setAspectRatio(4, 3)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 105: {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        CropImage.activity(Uri.fromFile(new File(mCurrentPhotoPath)))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());
//                        upload(mCurrentPhotoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                String picturePath = result.getUri().getPath();
                upload(picturePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                AppUtils.showalert(getActivity(), error.getMessage(), false);
            }
        }
    }

    public void upload(String path) {
        if (pd != null)
            pd.show();
        CognitoCachingCredentialsProvider credentialsProvider;
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity(),
                "ap-south-1:ce59be01-df08-4d7c-90ae-8bb61db4c094", // Identity Pool ID
                Regions.AP_SOUTH_1  // Region
        );
        File file = new File(path);
        final String uploadpath = "https://ubiapp.s3.ap-south-1.amazonaws.com/" + file.getName();
        Log.d(PostAdSecondStep.class.getName(), uploadpath);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getActivity());
        final TransferObserver observer = transferUtility.upload(
                "ubiapp",  //this is the bucket name on S3
                file.getName(), //this is the path and name
                file, //path to the file locally
                CannedAccessControlList.PublicRead //to make the file public
        );
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    if (pd != null)
                        pd.dismiss();
                    Log.d(PostAdSecondStep.class.getName(), uploadpath);
                    profile_img = uploadpath;
                    if (!TextUtils.isEmpty(profile_img)) {
                        Picasso.get().load(profile_img).into(profile_image);
                    }
                } else if (state.equals(TransferState.FAILED)) {
                    if (pd != null)
                        pd.dismiss();
                    AppUtils.showalert(getActivity(), "Something went wrong while uploading image", false);
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                pd.setMessage("Uploading " + ((bytesCurrent / bytesTotal) * 100) + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                AppUtils.showalert(getActivity(), "Something went wrong while uploading image, you can retry or skip for now", false);
                if (pd != null)
                    pd.dismiss();
            }
        });
    }

    private boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            this.checkStoragePermission();
        }
    }

    public boolean checkStoragePermission() {
        boolean read = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;

        boolean write = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;

        if (!read && !write) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Storage permission required")
                        .setMessage("UBI need your storage permission required for browse local storage")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        101);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            }
            return false;
        } else {
            return true;
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void showAlert(final Activity act) {
        new AlertDialog.Builder(act)
                .setMessage("Choose...")
                .setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (checkPermission())
                            takePictureFromGalary();
                        else
                            requestPermission();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNeutralButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (checkPermisionRequest()) {
                            takePictureFromCamera();
                        }
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void myProfile() {
        Map<String, String> params = new HashMap<>();
        if (AppUtils.isConnectingToInternet(getActivity())) {
            if (pd != null)
                pd.show();
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            String token = AppPreferences.getInstance().getToken(getActivity());
            Call<UserResponse> call = apiInterface.myProfile(token, params);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    Log.d("TAG", response.code() + "");
                    if (pd != null)
                        pd.dismiss();
                    if (response.code() == 200) {
                        if (response.body().code == 1) {
                            UserData data = response.body().payload;
                            if (data != null) {
                                firstnametv.setText(data.getFirst_name());
                                lastnametv.setText(data.getLast_name());
                                mobiletv.setText(data.getPhone());
                                emailtv.setText(data.getEmail());
                                citytv.setText(data.getCity());
                                companyname.setText(data.getCompany_name());
                                companyvideo.setText(data.getCompany_video());
                                about.setText(data.getAbout());
                                dob.setText(data.getBirth_date());
                                education.setText(data.getEducation());
                                if (!TextUtils.isEmpty(data.getProfile_img())) {
                                    Picasso.get().load(data.getProfile_img()).into(profile_image);
                                }
                                profile_img = data.getProfile_img();
                            }
                        } else {
                            AppUtils.showalert(getActivity(), response.body().message, false);
                        }
                    } else {
                        AppUtils.showalert(getActivity(), response.message(), false);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    call.cancel();
                    if (pd != null)
                        pd.dismiss();
                }
            });
        } else {
            AppUtils.Nointernetalert(getActivity());
        }
    }

    Calendar dobcal;
    String dobstr;
}
