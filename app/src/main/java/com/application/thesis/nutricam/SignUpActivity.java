package com.application.thesis.nutricam;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.application.thesis.nutricam.Classes.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    EditText fieldname, fieldage, fieldhtft, fieldhtin, fieldwtkg;
    MultiAutoCompleteTextView fieldallergies;
    Spinner fieldsex, fieldlifestyle, fieldhealthgoal;
    Button buttonsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            initializeFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFields() throws IOException {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<String> allergenList = databaseHelper.getAllMainCategory();
        fieldallergies = findViewById(R.id.etsu_allergy);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, allergenList);
        fieldallergies.setPadding(15,15,15,15);
        fieldallergies.setAdapter(arrayAdapter);
        fieldallergies.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    private void getAllergens(){

    }
}
