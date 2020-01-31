package com.example.mayday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String emailid = this.getMetaData(this, "emailid");
        final String password = this.getMetaData(this, "password");
        final Button mayday = findViewById(R.id.maydayButton);

        // modify this accordingly
        final String recipients = "";

        mayday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Mayday", "clicked");
                new MailSenderActivity().execute(emailid, password, recipients, "Test subject", "Test body");
            }
        });
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
