package com.g04autochef.model.storableDAO;

import com.g04autochef.model.TimeOfTheDay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Menu with meals for different periods of the day.
 */
public final class MenuDaily implements StorableDAO {

    private final String name;
    public final HashMap<TimeOfTheDay, Integer> people;
    public final HashMap<TimeOfTheDay, Recipe> recipes;

    public MenuDaily(final String name, final HashMap<TimeOfTheDay, Recipe> recipes, final HashMap<TimeOfTheDay, Integer> people) throws IllegalArgumentException {
        if (name == null) {throw new IllegalArgumentException("Daily menu name cannot be empty");}
        this.name = name;

        if (recipes.isEmpty()) {throw new IllegalArgumentException("Daily menu recipes cannot be empty");}
        this.recipes = recipes;

        if (people.isEmpty()) {throw new IllegalArgumentException("Daily menu number of people cannot be empty");}
        this.people = people;
    }

    public HashMap<TimeOfTheDay, Recipe> getRecipes() {return recipes;}

    public Recipe getDaytimeRecipe(final TimeOfTheDay daytime) {
        return recipes.get(daytime);
    }

    public Integer getDaytimePeople(final TimeOfTheDay daytime) {
        return people.get(daytime);
    }

    public String getName() {
        return name;
    }

    public Vector<IngredientRecipe> getAllIngredients() {
        final Vector<IngredientRecipe> ingredients = new Vector<>();
        Recipe recipe;
        for (TimeOfTheDay time : recipes.keySet()) {
            recipe = recipes.get(time);
            recipe.ponderateQuantity(people.get(time));
            ingredients.addAll(recipe.getIngredients());
        }
        return ingredients;
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }
}


