package com.application.thesis.nutricam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.application.thesis.nutricam.Classes.NutriProduct;
import com.application.thesis.nutricam.Classes.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
    Map<String, EditText> userFields = new HashMap<>();
    Map<String, EditText> standardFields = new HashMap<>();
    Map<String, Double> kCal2000 = new HashMap<>();
    EditText allergenset;
    String[] fieldList = {"calories", "total fat", "saturated fat", "trans fat"
            , "cholesterol", "sodium", "total carbohydrate", "dietary fiber"
            , "sugars", "protein"};
    private String processMode = "";
    private String[] resultArray;
    private ArrayList<TextView> arrayList = new ArrayList<>();
    private Integer caloricStandard;
    private Integer kCalStandard = 0;
    private NutriProduct nutriProduct = new NutriProduct();
    List<String> ingredients;
    List<String> detectedAllergens;
    ArrayList<String> allergens = new ArrayList<>();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        processMode = intent.getStringExtra("ProcessMode");
        Log.v("Process Mode:", processMode);
        if(processMode.equals("nutri"))
            ;
        else if(processMode.equals("ingre"))
            allergens = intent.getStringArrayListExtra("Ingredients");
        nutriProduct = (NutriProduct) getIntent().getSerializableExtra("NutriProduct");
        //ingredients = getIntent().getStringArrayListExtra("Ingredients");
        user.setFromSharedPref(this);
        initializeValues();
        setFieldValues();
    }

    private void setFieldValues(){
        userFields.get("ter").setText(String.format(Locale.ROOT, "%.0f", user.getTERValue()));
        standardFields.get("ter").setText(String.format(Locale.ROOT, "%.0f", 2000.0));
        for (String string: fieldList) {
            userFields.get(string).setText(String.format(Locale.ROOT, "%.2f%%", adjustedValues(string)));
            standardFields.get(string).setText(String.format(Locale.ROOT, "%.2f%%", standardValues(string)));
        }
        String output = "";
        for (String string: allergens) {
            output = string + ", " + output;
        }
        allergenset.setText(output);

    }

    private void initializeValues(){
        kCal2000.put("total fat", 65.0);
        kCal2000.put("saturated fat", 20.0);
        kCal2000.put("trans fat", 2.0);
        kCal2000.put("cholesterol", 0.3);
        kCal2000.put("sodium", 2.4);
        kCal2000.put("total carbohydrate", 300.0);
        kCal2000.put("dietary fiber", 25.0);
        kCal2000.put("sugars", 25.0);
        kCal2000.put("protein", 49.6);

        userFields.put("ter", (EditText) findViewById(R.id.etteruser));
        userFields.put("calories", (EditText) findViewById(R.id.etcaluser));
        userFields.put("total fat", (EditText) findViewById(R.id.ettotfatuser));
        userFields.put("saturated fat", (EditText) findViewById(R.id.etsatfatuser));
        userFields.put("trans fat", (EditText) findViewById(R.id.ettransfatuser));
        userFields.put("cholesterol", (EditText) findViewById(R.id.etcholesuser));
        userFields.put("sodium", (EditText) findViewById(R.id.etsoduser));
        userFields.put("total carbohydrate", (EditText) findViewById(R.id.etcarbuser));
        userFields.put("dietary fiber", (EditText) findViewById(R.id.etdietuser));
        userFields.put("sugars", (EditText) findViewById(R.id.etsuguser));
        userFields.put("protein", (EditText) findViewById(R.id.etprotuser));


        standardFields.put("ter", (EditText) findViewById(R.id.etterstand));
        standardFields.put("calories", (EditText) findViewById(R.id.etcalstand));
        standardFields.put("total fat", (EditText) findViewById(R.id.ettotfatstand));
        standardFields.put("saturated fat", (EditText) findViewById(R.id.etsatfatstand));
        standardFields.put("trans fat", (EditText) findViewById(R.id.ettransfatstand));
        standardFields.put("cholesterol", (EditText) findViewById(R.id.etcholesstand));
        standardFields.put("sodium", (EditText) findViewById(R.id.etsodstand));
        standardFields.put("total carbohydrate", (EditText) findViewById(R.id.etcarbstand));
        standardFields.put("dietary fiber", (EditText) findViewById(R.id.etdietstand));
        standardFields.put("sugars", (EditText) findViewById(R.id.etsugstand));
        standardFields.put("protein", (EditText) findViewById(R.id.etprotstand));

        allergenset = findViewById(R.id.etallergens);
    }

    private Double adjustedValues(String key){
        if(key.equals("calories"))
            return (nutriProduct.getAttributeDouble(key) / user.getTERValue()) * 100;
        else {
            if (!nutriProduct.checkAttribute(key)) {
                nutriProduct.setAttribute(key, "0.0");
                return 0.0;
            }
            return (nutriProduct.getAttributeDouble(key) / (user.getTERValue()/(2000/kCal2000.get(key)))) * 100;
        }
    }

    private Double standardValues(String key){
        if(key.equals("calories"))
            return (nutriProduct.getAttributeDouble(key) / 2000) * 100;
        else {
            if (!nutriProduct.checkAttribute(key)) {
                nutriProduct.setAttribute(key, "0.0");
                return 0.0;
            }
            return (nutriProduct.getAttributeDouble(key) / kCal2000.get(key)) * 100;
        }
    }

    private void determinekCalStandard(){
        boolean kCal2000 = false;
        boolean kCal2500 = false;

        for (int i = 0; i < resultArray.length; i++) {
            if(resultArray[i].equals("2000"))
                kCal2000 = true;
            if(resultArray[i].equals("2500"))
                kCal2500 = true;
            if(i == (resultArray.length - 1))
                kCal2000 = true;
        }

        if(kCal2000 && kCal2500)
            kCalStandard = 2000;
        else if(kCal2000)
            kCalStandard = 2000;
        else if(kCal2500)
            kCalStandard = 2500;
    }
}