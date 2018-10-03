package com.application.thesis.nutricam;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.application.thesis.nutricam.Classes.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class SignUpActivity extends AppCompatActivity {
    EditText fieldname, fieldsex, fielddob, fieldage, fieldhtcm, fieldwtkg, fieldlifestyle, fieldhealthgoal;
    MultiAutoCompleteTextView fieldallergies;
    AlertDialog alertDialog;

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
        fieldhtcm = findViewById(R.id.etsu_height);
        fieldwtkg = findViewById(R.id.etsu_weight);

        //EditText Name
        fieldname = findViewById(R.id.etsu_name);

        //EditText Sex
        fieldsex = findViewById(R.id.etsu_sex);
        fieldsex.setFocusable(false);
        fieldsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogSex();
            }
        });

        //EditText DOB & Age
        fieldage = findViewById(R.id.etsu_age);
        fielddob = findViewById(R.id.etsu_dob);
        fielddob.setFocusable(false);
        fielddob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogDOB();
            }
        });

        //EditText Lifestyle
        fieldlifestyle = findViewById(R.id.etsu_lifestyle);
        fieldlifestyle.setFocusable(false);
        fieldlifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogLifestyle();
            }
        });

        //EditText Health Goal
        fieldhealthgoal = findViewById(R.id.etsu_healthgoal);
        fieldhealthgoal.setFocusable(false);
        fieldhealthgoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogHealthGoal();
            }
        });

        //EditText Allergies
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<String> allergenList = databaseHelper.getAllMainCategory();
        fieldallergies = findViewById(R.id.etsu_allergy);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line, allergenList);
        fieldallergies.setAdapter(arrayAdapter);
        fieldallergies.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Button button = findViewById(R.id.btnsu_proceed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isComplete()){
                    setSharedPreferences();
                    Toast.makeText(SignUpActivity.this, "User Data Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(SignUpActivity.this, "Please Fill Up All Fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSharedPreferences(){
        SharedPreferences.Editor editUserData = getSharedPreferences(getString(R.string.sharedpreffile_userdata), MODE_PRIVATE).edit();
        editUserData.putString("name", fieldname.getText().toString());
        editUserData.putString("dob", fielddob.getText().toString());
        editUserData.putString("sex", fieldsex.getText().toString());
        editUserData.putString("heightcm", fieldhtcm.getText().toString());
        editUserData.putString("weightkg", fieldwtkg.getText().toString());
        editUserData.putString("lifestyle", fieldlifestyle.getText().toString());
        editUserData.putString("healthgoal", fieldhealthgoal.getText().toString());
        editUserData.putString("allergies", fieldallergies.getText().toString());
        editUserData.apply();
        SharedPreferences.Editor editSettings = getSharedPreferences(getString(R.string.sharedpreffile_settings), MODE_PRIVATE).edit();
        editSettings.putBoolean("MainFirst", false);
        editSettings.apply();
    }

    private boolean isComplete(){
        if(fieldname.getText().equals(""))
            return false;
        if(fieldsex.getText().equals(""))
            return false;
        if(fielddob.getText().equals(""))
            return false;
        if(fieldage.getText().equals("")
                || fieldage.getText().equals("0"))
            return false;
        if(fieldlifestyle.getText().equals(""))
            return false;
        if(fieldhealthgoal.getText().equals(""))
            return false;
        if(fieldhtcm.getText().equals("")
                || fieldhtcm.getText().equals("0.0")
                || fieldhtcm.getText().equals("0"))
            return false;
        if(fieldwtkg.getText().equals("")
                || fieldwtkg.getText().equals("0.0")
                || fieldwtkg.getText().equals("0"))
            return false;
        return true;
    }

    private void AlertDialogSex(){
        CharSequence[] values = {"Male", "Female"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select your Gender");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: fieldsex.setText("Male");
                        break;
                    case 1: fieldsex.setText("Female");
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void AlertDialogDOB(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String birthdate = dayOfMonth + "-" + (month + 1) + "-" + year;
                fielddob.setText(birthdate);
                Calendar dob = Calendar.getInstance();
                Calendar today = Calendar.getInstance();
                dob.set(year, month, dayOfMonth);
                int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
                    age--;
                Integer ageInt = age;
                fieldage.setText(String.format(Locale.ROOT, "%d", ageInt));
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void AlertDialogLifestyle(){
        CharSequence[] values = {"Inactive", "Sedentary", "Light", "Moderate", "Heavy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select your Lifestyle");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: fieldlifestyle.setText("Inactive");
                        break;
                    case 1: fieldlifestyle.setText("Sedentary");
                        break;
                    case 2: fieldlifestyle.setText("Light");
                        break;
                    case 3: fieldlifestyle.setText("Moderate");
                        break;
                    case 4: fieldlifestyle.setText("Heavy");
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void AlertDialogHealthGoal(){
        CharSequence[] values = {"Weight Gain", "Maintain Weight", "Weight Loss"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select your Health Goal");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: fieldhealthgoal.setText("Weight Gain");
                        break;
                    case 1: fieldhealthgoal.setText("Maintain Weight");
                        break;
                    case 2: fieldhealthgoal.setText("Weight Loss");
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
}
