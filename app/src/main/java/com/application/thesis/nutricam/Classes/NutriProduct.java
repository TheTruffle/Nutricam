package com.application.thesis.nutricam.Classes;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NutriProduct implements Serializable {
    private Map<String, String> dictionary = new HashMap<>();
    private String[] nutrientList = {};

    public NutriProduct() {
        super();
    }

    public void setAttribute(String attrID, String string){
        dictionary.put(attrID, string);
    }

    public String getAttribute(String attrID){
        return dictionary.get(attrID);
    }

    public boolean checkAttribute(String string){
        return dictionary.containsKey(string);
    }

    @Override
    public String toString() {
        String string = "";
        for (Map.Entry<String, String> entry : dictionary.entrySet()){
            string = string + "\n Key: " + entry.getKey() + "|| Value: " + entry.getValue();
        }
        return string;
    }

    //Use Case for Getting the value regardless g or mg
    public Double getAttributeDouble(String attrID){
        Log.v("NutriProduct Class", attrID + "-==-" + this.getAttribute(attrID));
        String useThis = "";
        String string = "";
        if(this.getAttribute(attrID) == null)
            useThis = "0.0";
        else
            useThis = convertNonDigits(this.getAttribute(attrID));
        if(isDigit(useThis))
            return Double.parseDouble(useThis);
        else
            switch (measureCase(this.getAttribute(attrID))){
                case 1:
                    string = useThis.substring(0, useThis.length() - 2);
                    return Double.parseDouble(String.format("%.2f", Double.parseDouble(string))) / 1000.0;
                case 2:
                    string = useThis.substring(0, useThis.length() - 1);
                    return Double.parseDouble(string);
                default:
                    return 0.0;
            }
    }

    public boolean ifAttributeExists(String key){
        return !dictionary.containsKey(key);
    }

    private boolean isDigit(String string){
        boolean checkflag = false;
        try{
            for (Character c: string.toCharArray()) {
                checkflag = Character.isDigit(c);
            }
        } catch (Exception e){
            ;
        }

        return checkflag;
    }

    private String convertNonDigits(String string){
        String toDigit = "";
        for (Character c : string.toCharArray()) {
            switch (c) {
                case 'O' :
                case 'o' :
                    toDigit = toDigit + '0';
                    break;
                case 'B' :
                    toDigit = toDigit + '8';
                    break;
                default:
                    toDigit = toDigit + c;
                    break;
                }
            }
        if(string.endsWith("9"))
            toDigit = toDigit + "g";
        return toDigit;
    }

    private Integer measureCase(String string){
        if(string == null)
            return 0;
        if(string.endsWith("mg"))
            return 1;
        else if(string.endsWith("g"))
            return 2;
        return 0;
    }
}