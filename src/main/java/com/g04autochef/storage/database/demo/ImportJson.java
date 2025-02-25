package com.g04autochef.storage.database.demo;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.data_access.filters.recipeCookingStyleFields.RecipeCookingStyleFieldName;
import com.g04autochef.data_access.filters.recipeFields.RecipeFieldName;
import com.g04autochef.data_access.filters.recipeTypeFields.RecipeTypeFieldName;

import com.g04autochef.model.storableDAO.*;
import com.g04autochef.storage.database.access.IngredientDAODB;
import com.g04autochef.storage.database.access.RecipeCookingStyleDAODB;
import com.g04autochef.storage.database.access.RecipeDAODB;
import com.g04autochef.storage.database.access.RecipeTypeDAODB;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class ImportJson {
    private JSONObject jsonObject;
    private JSONArray jsonObjectsArray;
    private final IngredientDAODB ingredientDAO = new IngredientDAODB();
    private final RecipeDAODB recipeDAO = new RecipeDAODB();
    private final RecipeCookingStyleDAODB recipeCookingStyleDAO = new RecipeCookingStyleDAODB();
    private final RecipeTypeDAODB recipeTypeDAO = new RecipeTypeDAODB();
    private Vector<String[]> missingIngredients;

    public ImportJson() throws AccessException {

    }

    public void openJsonFile(String JsonFilePath) throws IOException {
        String file = Files.readString(Path.of(JsonFilePath));
        JSONTokener tokener = new JSONTokener(file);
        this.jsonObjectsArray = new JSONArray(tokener);
    }
    private RecipeType getRecipeType() throws AccessException, FilterException {
        String type = this.jsonObject.getString("TypePlat");
        return addRecipeType(type);
    }
    private RecipeCookingStyle getCookingStyle() throws AccessException, FilterException {
        String style = this.jsonObject.getString("StylePlat");
        return addCookingStyle(style);
    }
    private Vector<String> getPreparationInstructions(){
        Vector<String> preparations = new Vector<>();
        JSONArray ja = new JSONArray(this.jsonObject.getJSONArray("Preparation"));
        for (Object instruction : ja) {
            preparations.add((String) instruction);
        }
        return preparations;
    }
    private Integer getNumberOfPeople(){
        return this.jsonObject.getInt("NombrePersonne");
    }
    private String getRecipeName(){
        return this.jsonObject.getString("Nom");
    }
    private Vector<IngredientRecipe> getIngredients() throws FilterException, AccessException {
        this.missingIngredients = new Vector<>();
        Vector<IngredientRecipe> ingredients = new Vector<>();
        Vector<Ingredient> ingredientsFiltered;
        Filter<Ingredient> filter;
        JSONArray ja = new JSONArray(this.jsonObject.getJSONArray("Ingredients"));
        for (Object obj : ja) {
            filter = new Filter<>();
            JSONObject jobj = (JSONObject) obj;
            filter.addField(new IngredientFieldName(getIngredientName(jobj)));
            ingredientsFiltered = ingredientDAO.select(filter);
            if(ingredientsFiltered.isEmpty()) {
                String[] temp = new String[]{getIngredientName(jobj), getUnit(jobj), getType(jobj), Integer.toString(getQuantity(jobj))};
                missingIngredients.add(temp);
            }else{
                ingredients.add(new IngredientRecipe(ingredientsFiltered.get(0), new Unit(getUnit(jobj)), getQuantity(jobj)));
            }
        }
        return manageDbIngredients(ingredients);
    }

    private String getUnit(JSONObject obj){
        return obj.getString("unite");
    }
    private String getIngredientName(JSONObject obj){
        return obj.getString("nom");
    }
    private String getType(JSONObject obj){
        return obj.getString("type");
    }
    private Integer getQuantity(JSONObject obj){
        return obj.getInt("quantite");
    }

    private Vector<IngredientRecipe> manageDbIngredients(Vector<IngredientRecipe> ingredients) {
        for(String[] list: this.missingIngredients) {
            try {
                Ingredient temp = addIngredient(list);
                ingredients.add(new IngredientRecipe(temp, new Unit(list[1]),Integer.parseInt(list[3])));
            } catch (AccessException ignored) {}
        }
        return ingredients;
    }

    private Ingredient addIngredient(String[] list) throws AccessException {
        Vector<Unit> units = new Vector<>();
        units.add(new Unit(list[1]));
        Ingredient ingredient = new Ingredient(list[0],new IngredientType(list[2]),units);
        ingredientDAO.insert(ingredient);
        return ingredient;
    }
    private RecipeCookingStyle addCookingStyle(String cookingStyle) throws FilterException, AccessException {
        Filter<RecipeCookingStyle> filter = new Filter<>();
        filter.addField(new RecipeCookingStyleFieldName(cookingStyle));
        Vector<RecipeCookingStyle> result = recipeCookingStyleDAO.select(filter);
        if (result.isEmpty()){
            RecipeCookingStyle temp = new RecipeCookingStyle(cookingStyle);
            recipeCookingStyleDAO.insert(temp);
            return temp;
        }
        return result.get(0);
    }
    private RecipeType addRecipeType(String cookingType) throws FilterException, AccessException {
        Filter<RecipeType> filter = new Filter<>();
        filter.addField(new RecipeTypeFieldName(cookingType));
        Vector<RecipeType> result = recipeTypeDAO.select(filter);
        if (result.isEmpty()){
            RecipeType temp = new RecipeType(cookingType);
            recipeTypeDAO.insert(temp);
            return temp;
        }
        return result.get(0);
    }

    private void getRecipe() throws AccessException, FilterException {
        Vector<IngredientRecipe> ingredients = getIngredients();
        Integer nbPeople = getNumberOfPeople();
        Vector<String> preparation = getPreparationInstructions();
        RecipeCookingStyle cookingStyle = getCookingStyle();
        RecipeType recipeType = getRecipeType();
        String name = getRecipeName();
        Recipe recipe = new Recipe(ingredients, preparation, nbPeople, name, recipeType, cookingStyle);
        Filter<Recipe> filter = new Filter<>();
        filter.addField(new RecipeFieldName(recipe.getName()));
        Vector<Recipe> recipeList = recipeDAO.select(filter);
        if(recipeList.isEmpty()) {
            recipeDAO.insert(recipe);
        }
    }

    public void startImportRecipes() throws AccessException, FilterException {
        for(Object jo:jsonObjectsArray){
            this.jsonObject = (JSONObject) jo;
            getRecipe();
        }
    }
}

