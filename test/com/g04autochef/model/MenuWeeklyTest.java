package com.g04autochef.model;

import com.g04autochef.model.storableDAO.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Vector;

class MenuWeeklyTest {

    private MenuDaily menuDaily;

    @BeforeEach
    public void makeMenuDaily(){
        final String name = "test";
        final var recipes = new HashMap<TimeOfTheDay, Recipe>();
        recipes.put(TimeOfTheDay.BREAKFAST, makeRecipe());
        final var people = new HashMap<TimeOfTheDay, Integer>();
        people.put(TimeOfTheDay.BREAKFAST, 1);
        this.menuDaily = new MenuDaily(name, recipes, people);
    }

    private Recipe makeRecipe(){
        final Vector<IngredientRecipe> ingredients = new Vector<>();
        final Unit unit = new Unit("name");
        final Vector<Unit> units = new Vector<>(); units.add(unit);
        final Ingredient ingredient =  new Ingredient("name", new IngredientType("name"), units);
        final IngredientRecipe ingredientRecipe =  new IngredientRecipe(ingredient, unit, 1);
        final Vector<String> instructions = new Vector<>(); instructions.add("Test");
        final String name = "Name";
        final RecipeType type = new RecipeType("type");
        ingredients.add(ingredientRecipe);
        return new Recipe(ingredients, instructions, 1, name, type);
    }

    @Test
    public void testMenuWeeklyNameEmpty(){
        final String name = "";
        final HashMap<DayOfTheWeek, MenuDaily> menus = new HashMap<>();
        menus.put(DayOfTheWeek.MONDAY, menuDaily);
        Assertions.assertThrows(IllegalArgumentException.class,() -> new MenuWeekly(name, menus));
    }

    @Test
    public void testMenuWeeklyNameNotEmpty(){
        final String name = "test";
        final HashMap<DayOfTheWeek, MenuDaily> menus = new HashMap<>();
        menus.put(DayOfTheWeek.MONDAY, menuDaily);
        new MenuWeekly(name, menus);
    }

    @Test
    public void testMenuWeeklyMenusEmpty(){
        final String name = "test";
        final HashMap<DayOfTheWeek, MenuDaily> menus = new HashMap<>();
        Assertions.assertThrows(IllegalArgumentException.class,() -> new MenuWeekly(name, menus));
    }
}