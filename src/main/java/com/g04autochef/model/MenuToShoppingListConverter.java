package com.g04autochef.model;

import com.g04autochef.model.storableDAO.MenuWeekly;
import com.g04autochef.model.storableDAO.ShoppingList;

/**
 * Class specially designed for converting a menu to a shopping list
 */
final public class MenuToShoppingListConverter {

    /**
     * Adds all ingredient of the Menu into the given existing ShoppingList
     * @param shoppingList the shopping list that we want to add ingredients
     * @param menuWeekly the menu that we want to convert
     */
    public static void AddMenuToShoppingList(final ShoppingList shoppingList, final MenuWeekly menuWeekly){
        shoppingList.addAllIngredients(menuWeekly.getAllIngredients());
    }

    /**
     * Convert the menu and add its ingredient to an new shopping list
     * @param menuWeekly the menu that we want to convert
     * @return create a new shopping list based on the menu
     */
    public static ShoppingList MenuToShoppingList(MenuWeekly menuWeekly){
        return new ShoppingList(menuWeekly.getName(), menuWeekly.getAllIngredients());
    }

}
