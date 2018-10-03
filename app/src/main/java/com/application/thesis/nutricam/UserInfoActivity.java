package com.application.thesis.nutricam;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.application.thesis.nutricam.Classes.DatabaseHelper;
import com.application.thesis.nutricam.Classes.User;

import java.io.IOException;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    EditText fieldname, fieldsex, fielddob, fieldage, fieldhtcm, fieldwtkg, fieldlifestyle, fieldhealthgoal, fieldbmi, fieldpa, fielddbw, fieldter;
    MultiAutoCompleteTextView fieldallergies;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setActivitySettings();
        user.setFromSharedPref(this);
        initializeFields();

    }

    private void setActivitySettings(){
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.sharedpreffile_settings), MODE_PRIVATE);
        //firstRun = sharedPreferences.getBoolean("UserInfoFirst", true);
    }

    private void initializeFields(){
        fieldname = findViewById(R.id.etui_name);
        fieldname.setText(user.getName());
        fieldname.setFocusable(false);

        fielddob = findViewById(R.id.etui_dob);
        fielddob.setText(user.getDob());
        fielddob.setFocusable(false);

        fieldage = findViewById(R.id.etui_age);
        fieldage.setText(String.valueOf(user.getAge()));
        fieldage.setFocusable(false);

        fieldsex = findViewById(R.id.etui_sex);
        fieldsex.setText(user.getSex());
        fieldsex.setFocusable(false);

        fieldhtcm = findViewById(R.id.etui_height);
        fieldhtcm.setText(String.valueOf(user.getHeightcm()));
        fieldhtcm.setFocusable(false);

        fieldwtkg = findViewById(R.id.etui_weight);
        fieldwtkg.setText(String.valueOf(user.getWeightkg()));
        fieldwtkg.setFocusable(false);

        fieldhealthgoal = findViewById(R.id.etui_healthgoal);
        fieldhealthgoal.setText(user.getHealthgoal());
        fieldhealthgoal.setFocusable(false);

        fieldlifestyle = findViewById(R.id.etui_lifestyle);
        fieldlifestyle.setText(user.getLifestyle());
        fieldlifestyle.setFocusable(false);

        fieldbmi = findViewById(R.id.etui_bmi);
        fieldbmi.setText(String.format("%1$,.0f", user.getBMI()));
        fieldbmi.setFocusable(false);

        fieldpa = findViewById(R.id.etui_pa);
        fieldpa.setText(String.format("%1$,.0f", user.getPAValue()));
        fieldpa.setFocusable(false);

        fielddbw = findViewById(R.id.etui_dbw);
        fielddbw.setText(String.format("%1$,.0f", user.getDBW()));
        fielddbw.setFocusable(false);

        fieldter = findViewById(R.id.etui_ter);
        fieldter.setText(String.format("%1$,.0f", user.getTERValue()));
        fieldter.setFocusable(false);

        fieldallergies = findViewById(R.id.etui_allergy);
        fieldallergies.setText(user.getAllergies());
        fieldallergies.setFocusable(false);

        Button btnback = findViewById(R.id.btnui_back);
    }
}
