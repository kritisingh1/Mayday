package com.example.mayday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import javax.mail.Quota;

import static com.example.mayday.CameraActivity.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity implements LocationListener {
    double coordinates[] = new double[2];
    static final int REQUEST_VIDEO_CAPTURE = 10;
    static final int REQUEST_CAMERA_ACCESS = 100;
    static final int REQUEST_LOCATION_ACCESS = 1;
    /* ========================================*/
    /* capture video
    /* ========================================*/
    private void dispatchTakeVideoIntent() {
        File rescueVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File rescueVideo = new File(rescueVideoDir.getPath() + File.separator + "Mayday.mp4");
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(rescueVideo));

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Log.d("video", "captured");
            // modify this accordingly
            final String recipients = "kritisingh1.ks@gmail.com";
            /* ========================================*/
            /* send rescue email
            /* ========================================*/
            sendRescueEmail(recipients);
            /* ========================================*/
            /* update UI
            /* ========================================*/
            final Button mayday = findViewById(R.id.maydayButton);
            mayday.setBackgroundColor(Color.parseColor("#3CB371"));
            mayday.setText("SOS sent!");
        }
    }

    protected void sendRescueEmail(String recipients) {
        /* ========================================*/
        /* data for email
        /* ========================================*/
        final String emailid = this.getMetaData(this, "emailid");
        final String password = this.getMetaData(this, "password");
        new MailSenderActivity().execute(emailid, password, recipients,
                "I need urgent help!",
                "Hello, \nIf you are receiving this email, I am in urgent need of help.\n" +
                        "My location coordinates are : (" + coordinates[0] + " , " + coordinates[1] + ").");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button mayday = findViewById(R.id.maydayButton);
        final Button reset = (Button) findViewById(R.id.resetButton);
        // get camera access
        if (!this.checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_ACCESS);
        }
        /* ========================================*/
        /* subscribe to user location
        /* ========================================*/
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // check necessary permissions
            if (!checkGPS) {
                Log.d("gps", "failed");
            }
            else if (!this.checkLocationPermission()) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ACCESS);
            }

            // request location
            if (this.checkLocationPermission()) {
                Location loc;
                Log.d("location request", "initiated");

                if (checkGPS) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (loc != null) {
                        coordinates[0] = loc.getLatitude();
                        coordinates[1] = loc.getLongitude();
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        } catch (SecurityException e) {
            Log.d("Location fetch", "failed");
            e.printStackTrace();
        }

        mayday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Mayday", "clicked");
                dispatchTakeVideoIntent();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mayday.setBackgroundColor(Color.TRANSPARENT);
                mayday.setBackgroundResource(R.drawable.custom_image_button);
                mayday.setText("Mayday");
            }
        });



        /* =============================================================
                display list of emergency contacts on button click
           =============================================================*/

        Button contactButton = (Button) findViewById(R.id.contactButton);

        contactButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // start a new intent
                Intent nextScreen = new Intent(getApplicationContext(), DisplayEmail.class);

                startActivity(nextScreen);
            }

        });


    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Updated latitude", String.valueOf(coordinates[0]));
        Log.d("Updated longitude", String.valueOf(coordinates[1]));
        coordinates[0] = location.getLatitude();
        coordinates[1] = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    /* ========================================*/
    /* check if location access via gps
    /* has already been granted to app
    /* ========================================*/
    public boolean checkLocationPermission() {
        return this.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /* ========================================*/
    /* check if app has camera access
    /* ========================================*/
    public boolean checkCameraPermission() {
        return this.checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /* ========================================*/
    /* handle user response to
    /* app permission requests
    /* ========================================*/
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("request code", String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_LOCATION_ACCESS: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("location permission", "denied");
                    finish();
                }
                return;
            }
            case REQUEST_CAMERA_ACCESS: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("camera permission", "denied");
                    finish();
                }
                return;
            }
        }
    }


    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Meta-data", "Unable to load meta-data: " + e.getMessage());
        }
        return null;
    }


    public void displayToastMessage(View view) {
        toastMsg("Help is arriving!");
    }

    public void toastMsg(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

}
