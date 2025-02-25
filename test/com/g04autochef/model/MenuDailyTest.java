package com.g04autochef.model;

import com.g04autochef.model.storableDAO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Vector;

class MenuDailyTest {

    private Recipe recipe;

    @BeforeEach
    @SuppressWarnings("unused")
    private void makeRecipe(){
        final Vector<IngredientRecipe> ingredients = new Vector<>();
        final Unit unit = new Unit("name");
        final Vector<Unit> units = new Vector<>(); units.add(unit);
        final Ingredient ingredient =  new Ingredient("name", new IngredientType("name"), units);
        final IngredientRecipe ingredientRecipe =  new IngredientRecipe(ingredient, unit, 1);
        final Vector<String> instructions = new Vector<>(); instructions.add("Test");
        final String name = "Name";
        final RecipeType type = new RecipeType("type");
        ingredients.add(ingredientRecipe);
        this.recipe = new Recipe(ingredients, instructions, 1, name, type);
    }

    @Test
    public void testMenuDailyNameNotEmpty(){
        final String name = "test";
        final var recipes = new HashMap<TimeOfTheDay, Recipe>();
        recipes.put(TimeOfTheDay.BREAKFAST, recipe);
        final var people = new HashMap<TimeOfTheDay, Integer>();
        people.put(TimeOfTheDay.BREAKFAST, 1);
        new MenuDaily(name, recipes, people);
    }

}