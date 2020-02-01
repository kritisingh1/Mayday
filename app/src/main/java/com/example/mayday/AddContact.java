package com.example.mayday;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class AddContact extends AppCompatActivity {

    EditText inputName;
    EditText inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        inputName = (EditText) findViewById(R.id.contactName);
        inputEmail = (EditText) findViewById(R.id.contactEmail);



    }
}
