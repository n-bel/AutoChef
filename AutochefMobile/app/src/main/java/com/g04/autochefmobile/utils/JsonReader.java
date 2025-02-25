package com.g04.autochefmobile.utils;

import android.content.Context;

import com.g04.autochefmobile.model.Ingredient;
import com.g04.autochefmobile.model.IngredientWithQuantity;
import com.g04.autochefmobile.model.ShoppingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * This class contains all the method for reading and creating a JSON file
 */
public class JsonReader {

    /**
     * @param context context activity
     * @param shoppingListName the name of the shopping list
     * @return the shopping list generated from the file
     */
    public ShoppingList readJson(Context context, String shoppingListName) throws IOException, JSONException {

        String contentJSON = read(context, shoppingListName);

        String name;
        JSONObject object;

        if (contentJSON != null){
            object = new JSONObject(contentJSON);
            name = object.get("N").toString();
        }else{
            return null;
        }

        if (create(context, name, contentJSON)){
            return null;
        }

        JSONArray array = object.getJSONArray("I");
        JSONArray arrayType;
        JSONArray arrayUnit;
        String unitType;
        String ingrName;
        int quantity;
        Ingredient ingredient;
        String type;

        Vector<IngredientWithQuantity> ingredientWithQuantityVector = new Vector<>();

        for (int h=0; h<array.length(); ++h){
            arrayType = (JSONArray) array.get(h);
            type = (String) arrayType.get(0);
            for (int i = 1; i < arrayType.length(); ++i) {
                arrayUnit = (JSONArray) arrayType.get(i);
                unitType = (String) arrayUnit.get(0);
                for (int j = 1; j < arrayUnit.length(); j += 2) {
                    ingrName = (String) arrayUnit.get(j);
                    quantity = (int) arrayUnit.get(j+1);
                    ingredient = new Ingredient(ingrName, type);
                    ingredientWithQuantityVector.add(new IngredientWithQuantity(ingredient, unitType, quantity));
                }
            }
        }
        return new ShoppingList(name, ingredientWithQuantityVector);
    }

    /**
     * @param context context activity
     * @param fileName the name of the shopping list
     * @return the content of the JSON file (structure and data)
     * this method will read the JSON file
     */
    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName+".json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException fileNotFound) {
            return null;
        }
    }

    /**
     * @param context context context activity
     * @param fileName the name of the shopping list
     * @param jsonString the content of the JSON file
     * @return boolean whether the file has been correctly created or not
     */
    private boolean create(Context context, String fileName, String jsonString){
        boolean flag = false;
        try {
            FileOutputStream fos = context.openFileOutput(fileName+".json",Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            flag = true;
        } catch (IOException ignored) {}
        return !flag;
    }

    public ShoppingList createAndReadJson(Context context, String contentJSON) throws JSONException, IOException {
        String name;
        JSONObject object;

        if (contentJSON != null){
            object = new JSONObject(contentJSON);
            name = object.get("N").toString();
        }else{
            return null;
        }

        if (create(context, name, contentJSON)){
            return null;
        }

        return readJson(context, name);
    }
}
