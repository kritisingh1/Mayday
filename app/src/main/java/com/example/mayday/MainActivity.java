package com.example.mayday;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements LocationListener {
    double coordinates[] = new double[2];
    final String emailid = this.getMetaData(this, "emailid");
    final String password = this.getMetaData(this, "password");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ========================================*/
        /* get user location
        /* ========================================*/
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        /* ========================================*/
        /* send rescue email */
        /* ========================================*/
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
