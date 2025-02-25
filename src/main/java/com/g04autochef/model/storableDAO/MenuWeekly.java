package com.g04autochef.model.storableDAO;

import com.g04autochef.model.DayOfTheWeek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Menu for a week:
 * Daily menu for each day of the week.
 */
public final class MenuWeekly implements StorableDAO {

    private final String name;
    private final HashMap<DayOfTheWeek, MenuDaily> menus;

    public MenuWeekly(final String name, final HashMap<DayOfTheWeek, MenuDaily> menus ) {
        if (name.isBlank()) {throw new IllegalArgumentException("Daily menu name cannot be empty");}
        this.name= name;

        if (menus.isEmpty()) {throw new IllegalArgumentException("Weekly menu cannot be empty");}
        this.menus = menus;
    }

    public MenuDaily getMenuDay(DayOfTheWeek day) {
        return menus.get(day);
    }

    public HashMap<DayOfTheWeek, MenuDaily> getAll() {
        return menus;
    }

    public String getName() {
        return name;
    }

    public HashMap<DayOfTheWeek, MenuDaily> getAllMenuDaily() {
        return menus;
    }

    public Vector<IngredientRecipe> getAllIngredients() {
        Vector<IngredientRecipe> ingredientsRecipe = new Vector<>();
        for (DayOfTheWeek day : menus.keySet()) {
            ingredientsRecipe.addAll(menus.get(day).getAllIngredients());
        }
        return ingredientsRecipe;
     }
    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }
}
