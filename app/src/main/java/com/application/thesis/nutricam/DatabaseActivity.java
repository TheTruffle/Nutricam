package com.application.thesis.nutricam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.application.thesis.nutricam.Classes.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        try {
            initializeFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFields() throws IOException {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<String> databaseContents = databaseHelper.getAllMainCategory();
        ListView listView = findViewById(R.id.listview1);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, databaseContents));
    }
}
