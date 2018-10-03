package com.application.thesis.nutricam.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class User {
    private String name;
    private String dob;
    private String sex;
    private Integer heightcm;
    private Integer weightkg;
    private String healthgoal;
    private String lifestyle;
    private String allergies;

    public User() {
        super();
    }

    public void setFromSharedPref(Context context){
        SharedPreferences getUserData =
                context.getSharedPreferences("UserData", MODE_PRIVATE);
        this.setName(getUserData.getString("name", "User"));
        this.setDob(getUserData.getString("dob", "N/A"));
        this.setSex(getUserData.getString("sex", "N/A"));
        this.setHeightcm(Integer.parseInt(getUserData.getString("heightcm", "0")));
        this.setWeightKg(Integer.parseInt(getUserData.getString("weightkg", "0")));
        this.setHealthGoal(getUserData.getString("healthgoal", "N/A"));
        this.setLifestyle(getUserData.getString("lifestyle", "N/A"));
        this.setAllergies(getUserData.getString("allergies", ""));
    }

    public void saveToSharedPref(Context context){
        SharedPreferences.Editor editUserData =
                context.getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editUserData.putString("name", this.name);
        editUserData.putString("dob", this.dob);
        editUserData.putString("sex", this.sex);
        editUserData.putString("heightcm", this.heightcm.toString());
        editUserData.putString("weightkg", this.weightkg.toString());
        editUserData.putString("lifestyle", this.lifestyle);
        editUserData.putString("healthgoal", this.healthgoal);
        editUserData.putString("allergies", this.allergies);
        editUserData.apply();

    }

    public void setName(String inName){
        this.name = inName;
    }
    public void setDob(String inDob) { this.dob = inDob; }
    public void setSex(String inSex) { this.sex = inSex; }
    public void setHeightcm(Integer inHeightcm) { this.heightcm = inHeightcm; }
    public void setWeightKg(Integer inWeightKg) { this.weightkg = inWeightKg; }
    public void setHealthGoal(String inHealthGoal) { this.healthgoal = inHealthGoal; }
    public void setLifestyle(String inLifestyle) { this.lifestyle = inLifestyle; }
    public void setAllergies(String inAllergies) { this.allergies = inAllergies; }

    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getSex() { return sex; }
    public Integer getHeightcm() { return heightcm; }
    public Integer getWeightkg() { return weightkg; }
    public String getHealthgoal() { return healthgoal; }
    public String getLifestyle() { return lifestyle; }
    public String getAllergies() {
        if(allergies.equals(null))
            return "";
        return allergies; }

    public Double getDBW(){
        if(sex.equals("Male"))
            return ((heightcm/100.0) * (heightcm/100.0)) * 22.0;
        else
            return ((heightcm/100.0) * (heightcm/100.0)) * 20.8;
    }

    public Double getBMI(){
        return weightkg / ((heightcm / 100.0) * (heightcm / 100.0));
    }

    public Double getTERValue(){
        switch(healthgoal){
            case "Maintain Weight":
                return weightkg * getPAValue();
            case "Weight Loss":
                return ((getDBW() + (0.25 * (weightkg - getDBW()))) * getPAValue()) - 500;
            case "Weight Gain":
                return ((getDBW() - (0.25 * (getDBW() - weightkg))) * getPAValue()) + 500;
        }
        return 0.0;
    }

    public Integer getAge(){
        Calendar caldob = Calendar.getInstance();
        Calendar calnow = Calendar.getInstance();

        String[] parts = dob.split("-");
        int dayOfBirth = Integer.parseInt(parts[0]);
        int monthOfBirth = Integer.parseInt(parts[1]);
        int yearOfBirth = Integer.parseInt(parts[2]);

        caldob.set(yearOfBirth, monthOfBirth, dayOfBirth);

        int age = calnow.get(Calendar.YEAR) - caldob.get(Calendar.YEAR);
        if(calnow.get(Calendar.DAY_OF_YEAR) < caldob.get(Calendar.DAY_OF_YEAR))
            age--;
        return age;

    }

    public Double getPAValue(){
        if(getAge() < 60)
            switch(lifestyle){
                case "Inactive":
                    return 27.5;
                case "Sedentary":
                    return 30.0;
                case "Light":
                    return 35.0;
                case "Moderate":
                    return 40.0;
                case "Heavy":
                    return 45.0;
            }
        else
            return 20.0;
        return 0.0;
        }

    public Double getCBW(){
        switch (healthgoal) {
            case "weight loss":
                return getDBW() + (0.25 * (weightkg - getDBW()));
            case "weight gain":
                return getDBW() - (0.25 * (getDBW() - weightkg));
        }
        return 0.0;
    }

    public List<String> getFoundAllergens(Context context) throws IOException {
        List<String> returnList = null;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        for (String allergy : allergies.split(",\\s+")) {
            for (String mainallergen : databaseHelper.getAllMainCategory()) {
                if(allergy.equals(mainallergen))
                    for (String suballergen : databaseHelper.getAllSubCategory(mainallergen)) {
                        returnList.add(suballergen);
                    }
            }
            returnList.add(allergies);
        }
        return returnList;
    }
}
