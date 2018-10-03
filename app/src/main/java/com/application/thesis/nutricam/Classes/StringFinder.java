
package com.application.thesis.nutricam.Classes;


import android.util.Log;

public class StringFinder {
    private String processtype = "";
    private String[] stringcontent = null;
    private String[] fieldList = {"calories", "total fat", "saturated fat", "trans fat"
            , "cholesterol", "sodium", "total carbohydrate", "dietary fiber"
            , "sugars", "protein"};
    private String[] dictionary = {"serving", "size", "servings", "container"
            , "calories", "total", "saturated", "trans", "cholesterol"
            , "sodium", "carbohydrate", "dietary", "fiber", "sugars", "protein"};

    public StringFinder() {
        super();
    }

    public String getProcesstype(){
        return processtype;
    }

    private void cleanArray(){
        for (String srcString: dictionary){
            Integer indexContent = 0;
            for (String inString: stringcontent){
                Integer index = 0;
                Integer score = 0;
                for (Character c : inString.toLowerCase().toCharArray()) {
                    if(index.equals(srcString.length()))
                        break;
                    if(c.equals(srcString.charAt(index))) {
                        score = score + 1;
                        index = index + 1;
                    }
                }
                Log.v("StringFinder Class",
                        "Source: " + srcString
                                + " || Found: " + inString
                                + "|| Score: " + score.toString());
                if(!score.equals(srcString.length()))
                    if (srcString.length() > inString.length())
                        if (score > srcString.length() / 2)
                            stringcontent[indexContent] = srcString;
                indexContent = indexContent + 1;
            }
        }
    }

    public NutriProduct getNutriData(){
        cleanArray();
        NutriProduct nutriProduct = new NutriProduct();
        String prevString = "";
        Integer index = 0;
        for (String string: stringcontent) {
            if(index.equals(stringcontent.length))
                break;
            switch(string.toLowerCase()){
                case "size":
                    if(nutriProduct.ifAttributeExists("serving size"))
                        nutriProduct.setAttribute("serving size", convertNonDigits(stringcontent[index + 1]) + convertNonDigits(stringcontent[index + 2]));
                    break;
                case "container":
                case "pack":
                    if(nutriProduct.ifAttributeExists("servings per container"))
                        nutriProduct.setAttribute("servings per container", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "calories":
                    if(isDigit(stringcontent[index + 1]))
                        if(nutriProduct.ifAttributeExists("calories"))
                            nutriProduct.setAttribute("calories", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "fat":
                    switch (prevString){
                        case "from":
                            if(nutriProduct.ifAttributeExists("calories from fat"))
                                nutriProduct.setAttribute("calories from fat", convertNonDigits(stringcontent[index + 1]));
                            break;
                        case "total":
                            if(nutriProduct.ifAttributeExists("total fat"))
                                nutriProduct.setAttribute("total fat", convertNonDigits(stringcontent[index + 1]));
                            break;
                        case "saturated":
                        case "saturates":
                            if(nutriProduct.ifAttributeExists("saturated fat"))
                                nutriProduct.setAttribute("saturated fat", convertNonDigits(stringcontent[index + 1]));
                            break;
                        case "trans":
                            if(nutriProduct.ifAttributeExists("trans fat"))
                                nutriProduct.setAttribute("trans fat", convertNonDigits(stringcontent[index + 1]));
                            break;
                    }
                    break;
                case "cholesterol":
                    if(nutriProduct.ifAttributeExists("cholesterol"))
                        nutriProduct.setAttribute("cholesterol", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "sodium":
                    if(nutriProduct.ifAttributeExists("sodium"))
                        nutriProduct.setAttribute("sodium", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "carbohydrate":
                    if(nutriProduct.ifAttributeExists("total carbohydrate"))
                        nutriProduct.setAttribute("total carbohydrate", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "fiber":
                case "fibre":
                    if(nutriProduct.ifAttributeExists("dietary fiber"))
                        nutriProduct.setAttribute("dietary fiber", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "sugars":
                    if(nutriProduct.ifAttributeExists("sugars"))
                        nutriProduct.setAttribute("sugars", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "protein":
                    if(nutriProduct.ifAttributeExists("protein"))
                        nutriProduct.setAttribute("protein", convertNonDigits(stringcontent[index + 1]));
                    break;
                case "2000":
                    if(nutriProduct.ifAttributeExists("kCal"))
                        nutriProduct.setAttribute("kCal", "2000");
                    break;
                case "2500":
                    if(nutriProduct.ifAttributeExists("kCal"))
                        nutriProduct.setAttribute("kCal", "2500");
                    break;
            }
            index = index + 1;
            prevString = string.toLowerCase();
        }
        for (String string: fieldList) {
            if(!nutriProduct.checkAttribute(string))
                nutriProduct.setAttribute(string, "0g");
        }

        return nutriProduct;
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
            toDigit = string.substring(0, string.length() - 1) + 'g';
        return toDigit;
    }

    private boolean isDigit(String string){
        for(char c : string.toCharArray())
            return Character.isDigit(c);
        return false;
    }

    public void setProcessMode(String processMode) {
        processtype = processMode;
    }

    public void setStringContent(String[] string) {
        stringcontent = string;
    }


}
