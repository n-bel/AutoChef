package com.g04autochef.model;

import com.g04autochef.model.storableDAO.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Vector;

public class MenuToShoppingListConverterTest {
    ShoppingList shoppingList;
    MenuWeekly menuWeekly;

    @BeforeEach
    public void initValue(){
        shoppingList = new ShoppingList("Liste Test", new Vector<>());
    }

    @Test
    public void testMenuToShoppingList(){
        Recipe recipe = createRecipe();

        HashMap<TimeOfTheDay, Recipe> recipeHashMap = new HashMap<>();
        HashMap<TimeOfTheDay, Integer> nbPeople = new HashMap<>();

        recipeHashMap.put(TimeOfTheDay.BREAKFAST, recipe);
        nbPeople.put(TimeOfTheDay.BREAKFAST, 5);

        HashMap<DayOfTheWeek, MenuDaily> menuDailyHashMap = new HashMap<>();

        menuDailyHashMap.put(DayOfTheWeek.MONDAY, new MenuDaily("Tortilla Royale 1", recipeHashMap, nbPeople));

        menuWeekly = new MenuWeekly("Tortilla Royale 2", menuDailyHashMap);

        shoppingList = MenuToShoppingListConverter.MenuToShoppingList(menuWeekly);

        Assertions.assertEquals("Tortilla Royale 2", shoppingList.getName());
        Assertions.assertEquals(new ShoppingList("test", menuWeekly.getAllIngredients()).getIngredients(), shoppingList.getIngredients());
    }

    private Recipe createRecipe() {
        Vector<Unit> unitVector = new Vector<>();
        unitVector.add(new Unit("gramme"));

        Vector<IngredientRecipe> ingredientRecipeVector = new Vector<>();
        ingredientRecipeVector.add(new IngredientRecipe(new Ingredient("tortilla", new IngredientType("autre"), unitVector), unitVector.get(0), 2));
        ingredientRecipeVector.add(new IngredientRecipe(new Ingredient("pomme de terre", new IngredientType("autre"), unitVector), unitVector.get(0), 2));

        Vector<String> instructions = new Vector<>();
        instructions.add("épluchez les pommes de terre");
        instructions.add("cuire les pommes de terre");

        Recipe recipe = new Recipe(ingredientRecipeVector, instructions, 5, "Tortilla Royale", new RecipeType("autre"), new RecipeCookingStyle("accompagnement"));
        return recipe;
    }

}
