package com.g04autochef.model;

import com.g04autochef.model.storableDAO.*;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

class ShoppingListTest {

    ShoppingList shoppingList;
    Ingredient ingredientTest1;
    Ingredient ingredientTest2;
    Ingredient ingredientTest3;
    Ingredient ingredientTest4;
    IngredientRecipe ingredientRecipeTest1;
    IngredientRecipe ingredientRecipeTest2;
    IngredientRecipe ingredientRecipeTest3;
    IngredientRecipe ingredientRecipeTest4;
    IngredientRecipe ingredientRecipeTest5;
    Vector<Unit> unitVector;

    @BeforeEach
    public void initValue(){
        shoppingList = new ShoppingList("Liste Test", new Vector<>());
        unitVector = new Vector<>();
        unitVector.add(new Unit("gramme"));
        unitVector.add(new Unit("cuillère"));
        unitVector.add(new Unit("unité"));
        ingredientTest1 = new Ingredient("tortilla", new IngredientType("Autre"), unitVector);
        ingredientTest2 = new Ingredient("purée de pommes de terre", new IngredientType("Autre"), unitVector);
        ingredientTest3 = new Ingredient("Sel", new IngredientType("Epice"), unitVector);
        ingredientTest4 = new Ingredient("Pomme", new IngredientType("Fruit"), unitVector);

        ingredientRecipeTest1 = new IngredientRecipe(ingredientTest1, unitVector.get(0), 2);
        ingredientRecipeTest2 = new IngredientRecipe(ingredientTest1, unitVector.get(0), 3);
        ingredientRecipeTest3 = new IngredientRecipe(ingredientTest2, unitVector.get(0), 4);
        ingredientRecipeTest4 = new IngredientRecipe(ingredientTest3, unitVector.get(1), 4);
        ingredientRecipeTest5 = new IngredientRecipe(ingredientTest4, unitVector.get(2), 4);

    }


    @Test
    public void testShoppingListEmptyName(){
        final Vector<IngredientRecipe> ingredients = new Vector<>();
        ingredients.add(ingredientRecipeTest1);
        final String name = "  ";
        Assertions.assertThrows(IllegalArgumentException.class,() -> new ShoppingList(name, ingredients));
    }

    @Test
    public void testAddSameIngredient(){

        Integer expectedQuantity = ingredientRecipeTest1.getQuantity()+ingredientRecipeTest2.getQuantity();
        shoppingList.addIngredient(ingredientRecipeTest1);
        shoppingList.addIngredient(ingredientRecipeTest2);
        System.out.println(shoppingList.getIngredients().get(0).getQuantity());
        Assertions.assertEquals(shoppingList.getIngredients().get(0).getQuantity(), expectedQuantity);
    }

    @Test
    public void testAddDifferentIngredient(){
        Vector<IngredientRecipe> ingredientRecipeVector = new Vector<>();
        shoppingList.addIngredient(ingredientRecipeTest1);
        shoppingList.addIngredient(ingredientRecipeTest3);
        ingredientRecipeVector.add(ingredientRecipeTest1);
        ingredientRecipeVector.add(ingredientRecipeTest3);
        Assertions.assertEquals(shoppingList.getIngredients(), ingredientRecipeVector);
    }

    @Test
    public void testGetSortedIngredients() {
        shoppingList.addIngredient(ingredientRecipeTest1);
        shoppingList.addIngredient(ingredientRecipeTest3);
        shoppingList.addIngredient(ingredientRecipeTest4);
        shoppingList.addIngredient(ingredientRecipeTest5);
        HashMap<String, HashMap<String, Vector<IngredientRecipe>>> sortedIngredients = shoppingList.getSortedIngredients();
        for (String type : sortedIngredients.keySet()) {
            for (String unit : sortedIngredients.get(type).keySet()) {
                for (IngredientRecipe ingr : sortedIngredients.get(type).get(unit)) {
                    System.out.println(ingr.getName());
                    Assertions.assertEquals(ingr.getType(), type);
                    Assertions.assertEquals(ingr.getUnit().getName(), unit);
                }
            }
        }
    }

    @Test
    public void testGetSortedIngredientsAllPresent() {
        Vector<IngredientRecipe> ingredients = new Vector<>();
        Vector<IngredientRecipe> ingredientsToAdd = new Vector<>(List.of(ingredientRecipeTest1, ingredientRecipeTest3, ingredientRecipeTest4 ,ingredientRecipeTest5));
        shoppingList.addAllIngredients(ingredientsToAdd);
        HashMap<String, HashMap<String, Vector<IngredientRecipe>>> sortedIngredients = shoppingList.getSortedIngredients();
        System.out.println(sortedIngredients.keySet());
        for (String type : sortedIngredients.keySet()) {
            System.out.println(sortedIngredients.get(type).keySet());
            for (String unit : sortedIngredients.get(type).keySet()) {
                ingredients.addAll(sortedIngredients.get(type).get(unit));
            }
        }
        Assertions.assertTrue(ingredients.containsAll(ingredientsToAdd));
    }
}