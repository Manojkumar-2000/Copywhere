package com.miniproject.copywhere;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class MainActivity extends AppCompatActivity {
    String imageFilePath;
    ImageButton Camera_button, Photos_button, Go_button;
    Uri image_uri;
    //Activity result contract to select an image from the storage
    //here the input type is string
    //the string determines the mime type of the file to be selected
    //here the mime type is image
    //this activity returns the selected image's URI
    ImageView Selected_image;
        ActivityResultLauncher<String> get_picture = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> CropImage.activity(result)
                .start(MainActivity.this));

    //Activity contract to request the required permissions
    //here the input type is string
    //the input string being the permission required
    //this activity request the permission and returns a boolean value
    ActivityResultLauncher<String> requestPermissionLauncher;

    {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) {
                Toast.makeText(this, "Permission required for performing the activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Activity contract to take picture using the camera app
    //here the input is a content uri
    //the activity captures the image and stores the image in the provided contend uri
    //returns a boolean value to tell whether the image is successfully stored in the provided uri or not
    ActivityResultLauncher<Uri>  take_picture = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result)
                CropImage.activity(image_uri)
                        .start(MainActivity.this);
            else {
                Log.d("cancel","user canceled the the camera acrivity");
            }
        }
    });
   //This function creates a temp image file to store the picture returned by the camera activity
   //This image is stored in the apps private storage area
    private File createImageFile() throws IOException {
        //string variable to store the current time stamp
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        //string variable to store the name of the temp file
        String imageFileName = "IMG_" + timeStamp + "_";//the result image file name wil be IMG_"the_current_time_stamp"-_
        File storageDir =  getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Binding the UI elements to variables
        Camera_button = findViewById(R.id.camera_button);
        Photos_button = findViewById(R.id.photo_album_button);
        Go_button = findViewById(R.id.go_button);
        Selected_image = findViewById(R.id.selected_image);
        //When this button is pressed the image album is shown to pick a picture
        //this activity requires storage permissions
        Photos_button.setOnClickListener(v -> {
            //Checking whether the permissions is already given
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //if allowed show photo album to select an image
                get_picture.launch("image/*");
            }
            else
                //if storage is not allowed then prompt the user to allow storage permission
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        });
        //when this button is pressed Camera activity is started to capture a picture
        //this activity requires camera permissions
        Camera_button.setOnClickListener(v -> {
            // check if camera permission is already allowed
            // if allowed then open camera to capture image
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                take_picture();
            //if not allowed then prompt the user to allow camera permission
            else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }
            else
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        });
        Go_button.setOnClickListener(v -> {
            Intent intent= new Intent(MainActivity.this,result_activity.class);
            String imguri=image_uri.toString();
            intent.putExtra("image_uri",imguri);
            startActivity(intent);
        });

    }
    private void take_picture() {
        File photoFile = null;
        try {
            //creating a image file to store the image captured by the camera activity
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            // getting a content URI to pass to the camera activity
            image_uri = FileProvider.getUriForFile(this, "com.miniproject.copywhere.provider", photoFile);
            take_picture.launch(image_uri);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                image_uri=resultUri;
                Selected_image.setImageURI(resultUri);
                Go_button.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: ",error );
            }
        }
    }


}
