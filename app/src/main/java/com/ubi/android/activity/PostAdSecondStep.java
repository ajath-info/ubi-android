package com.ubi.android.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ubi.android.R;
import com.ubi.android.models.Attachments;
import com.ubi.android.utils.AppUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostAdSecondStep extends AppCompatActivity {
    ProgressDialog pd;
    LinearLayout attachlay;
    ArrayList<Attachments> attachments = new ArrayList<Attachments>();
    LinearLayout nextlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad_second_step);
        attachlay = findViewById(R.id.attachlay);
        nextlay = findViewById(R.id.nextlay);
        findViewById(R.id.backlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pd = new ProgressDialog(PostAdSecondStep.this);
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init();
        nextlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uploadpath = "";
                StringBuilder builder1 = new StringBuilder();
                for (int i = 0; i < attachments.size(); i++) {
                    if (i == attachments.size() - 1)
                        builder1.append(attachments.get(i).url);
                    else
                        builder1.append(attachments.get(i).url).append("|");
                }
                uploadpath = builder1.toString();
                if (TextUtils.isEmpty(uploadpath)) {
                    AppUtils.showalert(PostAdSecondStep.this, "Please add Ad images and then try", false);
                } else {
                    Intent intent = new Intent(PostAdSecondStep.this, PostAdThirdStepActivity.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("uploadpath", uploadpath);
                    startActivityForResult(intent, 123);
                }
            }
        });
    }

    private void init() {
        findViewById(R.id.cameralay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermisionRequest()) {
                    takePictureFromCamera();
                }
            }
        });

        findViewById(R.id.folderlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission())
                    takePictureFromGalary();
                else
                    requestPermission();
            }
        });
    }

    // Here code for Camera photo

    private void takePictureFromCamera() {
//        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePicture.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePicture, 2);
//        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, 105);
            }
        }
    }

    private boolean checkPermisionRequest() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(PostAdSecondStep.this, Manifest.permission.CAMERA);
            int READ_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(PostAdSecondStep.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int WRITE_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(PostAdSecondStep.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraPermission == PackageManager.PERMISSION_DENIED ||
                    READ_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED ||
                    WRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(PostAdSecondStep.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
                return false;
            }
        }
        return true;
    }

    boolean isforcamera = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPermission())
                takePictureFromCamera();
            else {
                isforcamera = true;
                requestPermission();
            }
        } else if (requestCode == 20 && grantResults.length == 0) {
            Toast.makeText(PostAdSecondStep.this, "Camera permission denied", Toast.LENGTH_SHORT).show();

        } else if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPermission())
                takePictureFromGalary();
            else
                requestPermission();
        } else if (requestCode == 101 && grantResults.length == 0) {
            Toast.makeText(PostAdSecondStep.this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePictureFromGalary() {
        Intent takePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takePhoto.setType("image/*");
        startActivityForResult(takePhoto, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        if (requestCode == 2296) {
            if (isforcamera) {
                isforcamera = false;
                takePictureFromCamera();

            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    takePictureFromGalary();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = getContentResolver().query(selectedImage,
//                                filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String picturePath = cursor.getString(columnIndex);
//                        cursor.close();
//                        upload(picturePath);
                        CropImage.activity(selectedImage)
                                .setAspectRatio(4, 3)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 105: {
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    try {
//                        Bundle extras = data.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        if (imageBitmap != null) {
//
//                        }
                        CropImage.activity(Uri.fromFile(new File(mCurrentPhotoPath)))
                                .setAspectRatio(4, 3)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(this);
//                        upload(mCurrentPhotoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(resultUri,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
                String picturePath = result.getUri().getPath();
                upload(picturePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                AppUtils.showalert(PostAdSecondStep.this, error.getMessage(), false);
            }
        }
    }

    public void upload(String path) {
        if (pd != null)
            pd.show();
        CognitoCachingCredentialsProvider credentialsProvider;
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-south-1:ce59be01-df08-4d7c-90ae-8bb61db4c094", // Identity Pool ID
                Regions.AP_SOUTH_1  // Region
        );
        File file = new File(path);
        final String uploadpath = "https://ubiapp.s3.ap-south-1.amazonaws.com/" + file.getName();
        Log.d(PostAdSecondStep.class.getName(), uploadpath);
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
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
//                    Intent intent = new Intent(PostAdSecondStep.this, PostAdThirdStepActivity.class);
//                    intent.putExtras(getIntent());
//                    intent.putExtra("uploadpath", uploadpath);
//                    startActivityForResult(intent, 123);
                    Attachments attachment = new Attachments();
                    attachment.name = file.getName();
                    attachment.url = uploadpath;
                    attachments.add(attachment);
                    addLayout();
                } else if (state.equals(TransferState.FAILED)) {
                    if (pd != null)
                        pd.dismiss();
                    AppUtils.showalert(PostAdSecondStep.this, "Something went wrong while uploading image", false);
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                pd.setMessage("Uploading " + ((bytesCurrent / bytesTotal) * 100) + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                AppUtils.showalert(PostAdSecondStep.this, "Something went wrong while uploading image, you can retry or skip for now", false);
                if (pd != null)
                    pd.dismiss();
            }
        });
    }

    private boolean checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
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

    public void checkStoragePermission() {
//        boolean read = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED;
//
//        boolean write = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Storage permission required")
                    .setMessage("UBI need your storage permission required for browse local storage")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(PostAdSecondStep.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    101);
                        }
                    })
                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        Log.d(PostAdSecondStep.class.getName(), storageDir.getPath());
//        File f = new File(storageDir, imageFileName + ".jpg");
//        if (!f.getParentFile().exists())
//            f.getParentFile().mkdirs();
//        if (!f.exists())
//            f.createNewFile();
////        File image = File.createTempFile(
////                imageFileName,  // prefix
////                ".jpg",         // suffix
////                storageDir      // directory
////        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = f.getAbsolutePath();
//        return f;
//    }

    private void addLayout() {
        attachlay.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < attachments.size(); i++) {
            View v = vi.inflate(R.layout.attachment_item, null);
            TextView itemname = v.findViewById(R.id.itemname);
            itemname.setText(attachments.get(i).name);
            ImageView delete = v.findViewById(R.id.delete);
            int finalI = i;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachments.remove(finalI);
                    addLayout();
                }
            });
            attachlay.addView(v);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}