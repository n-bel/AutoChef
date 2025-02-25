package com.g04.autochefmobile.model;
import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.junit.Test;
import org.junit.Before;

public class ShoppingListTest {

    ShoppingList shoppingList;
    Ingredient ingredientTest1;
    Ingredient ingredientTest2;
    IngredientWithQuantity ingredientRecipeTest1;
    IngredientWithQuantity ingredientRecipeTest2;
    IngredientWithQuantity ingredientRecipeTest3;
    Vector<String> unitVector;

    @Before
    public void initValue(){
        shoppingList = new ShoppingList("Liste Test", new Vector<>());
        unitVector = new Vector<>();
        unitVector.add("gramme");
        unitVector.add("cuillère");
        unitVector.add("unité");
        ingredientTest1 = new Ingredient("tortilla", "Autre");
        ingredientTest2 = new Ingredient("purée de pommes de terre", "Autre");

        ingredientRecipeTest1 = new IngredientWithQuantity(ingredientTest1, "gramme", 2);
        ingredientRecipeTest2 = new IngredientWithQuantity(ingredientTest1, "gramme", 3);
        ingredientRecipeTest3 = new IngredientWithQuantity(ingredientTest2, "gramme", 4);

    }

    @Test
    public void testAddSameIngredient(){
        Integer expectedQuantity = ingredientRecipeTest1.getQuantity()+ingredientRecipeTest2.getQuantity();
        shoppingList.addIngredient(ingredientRecipeTest1);
        shoppingList.addIngredient(ingredientRecipeTest2);
        assertEquals(shoppingList.getIngredients().get(0).getQuantity(), expectedQuantity);
    }

    @Test
    public void testAddDifferentIngredient(){
        Vector<IngredientWithQuantity> ingredientRecipeVector = new Vector<>();
        shoppingList.addIngredient(ingredientRecipeTest1);
        shoppingList.addIngredient(ingredientRecipeTest3);
        ingredientRecipeVector.add(ingredientRecipeTest1);
        ingredientRecipeVector.add(ingredientRecipeTest3);
        assertEquals(shoppingList.getIngredients(), ingredientRecipeVector);
    }
}