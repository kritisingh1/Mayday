package com.example.mayday;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DisplayEmail extends AppCompatActivity {

    private List<Contacts> contactsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_email);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        cAdapter = new ContactsAdapter(contactsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);

        prepareContactData();
    }

    private void prepareContactData() {
        Contacts contact = new Contacts("abc", "abc@example.com");
        contactsList.add(contact);

        contact = new Contacts("def", "def@example.com");
        contactsList.add(contact);

        contact = new Contacts("ghi", "ghi@example.com");
        contactsList.add(contact);

        cAdapter.notifyDataSetChanged();
    }

}
