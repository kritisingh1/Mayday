package com.example.mayday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    double coordinates[] = new double[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            // request location
            if (checkGPS && this.checkLocationPermission()) {
                Log.d("location request", "initiated");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (loc != null) {
                    coordinates[0] = loc.getLatitude();
                    coordinates[1] = loc.getLongitude();
                }
            }
        } catch (SecurityException e) {
            Log.d("Location fetch", "failed");
            e.printStackTrace();
        }

        /* ========================================*/
        /* send rescue email
        /* ========================================*/
        final String emailid = this.getMetaData(this, "emailid");
        final String password = this.getMetaData(this, "password");
        final Button mayday = findViewById(R.id.maydayButton);
        // modify this accordingly
        final String recipients = "babyyodaisop@gmail.com";

        mayday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Mayday", "clicked");
                new MailSenderActivity().execute(emailid, password, recipients,
                        "I need urgent help!",
                        "Hello, \nIf you are receiving this email, I am in urgent need of help.\n" +
                                "My location coordinates are : (" + coordinates[0] + " , " + coordinates[1] + ").");
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
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /* ========================================*/
    /* handle user response to location
    /* access permission
    /* ========================================*/
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("request code", String.valueOf(requestCode));
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("location permission", "denied");
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
}
