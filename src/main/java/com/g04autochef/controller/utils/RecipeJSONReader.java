package com.g04autochef.controller.utils;

import com.g04autochef.model.storableDAO.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

/**
 * Read JSON file to build a Recipe
 */
public class RecipeJSONReader {

    private JSONObject jObject;


    public RecipeJSONReader(String path) throws IOException {
        String f = Files.readString(Path.of(path));
        JSONTokener tokener = new JSONTokener(f);
        jObject = new JSONObject(tokener);
    }

    public Vector<Vector<String>> getIngredientsName() {
        Vector<Vector<String>> ingredientsName = new Vector<>();
        Vector<String> ingredient;
        for (Object o : jObject.getJSONArray("Ingredients")) {
            JSONObject jobj = (JSONObject) o;
            ingredient = new Vector<>();
            ingredient.add(jobj.getString("nom"));
            ingredient.add(jobj.getString("unite"));
            ingredient.add(Integer.toString(jobj.getInt("quantite")));
            ingredientsName.add(ingredient);
        }
        return ingredientsName;
    }

    public Recipe getRecipe(Vector<IngredientRecipe> ingredients) {
        String name = jObject.getString("Nom");
        RecipeType recipeType = new RecipeType(jObject.getString("TypePlat"));
        RecipeCookingStyle cookingstyle = new RecipeCookingStyle(jObject.getString("StylePlat"));
        int nbPeople = jObject.getInt("NombrePersonne");

        Vector<String> prep = new Vector<>();
        JSONArray jArray = new JSONArray(jObject.getJSONArray("Preparation"));
        for (Object o : jArray) {
            prep.add((String) o);
        }
        return new Recipe(ingredients, prep, nbPeople, name, recipeType, cookingstyle);
    }

}
