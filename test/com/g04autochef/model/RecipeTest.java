package com.g04autochef.model;

import com.g04autochef.model.storableDAO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Vector;

class RecipeTest {

    private Recipe testRecipe;

    @BeforeEach
    public void makeTestRecipe(){
        final Vector<IngredientRecipe> ingredients = new Vector<>();
        final Unit unit = new Unit("name");
        final Vector<Unit> units = new Vector<>(); units.add(unit);
        final Ingredient ingredient =  new Ingredient("name", new IngredientType("name"), units);
        final IngredientRecipe ingredientRecipe =  new IngredientRecipe(ingredient, unit, 1);
        final Vector<String> instructions = new Vector<>();
        instructions.add("test");
        final String name = "name";
        final RecipeType type = new RecipeType("type");
        ingredients.add(ingredientRecipe);
        testRecipe = new Recipe(ingredients, instructions, 1, name, type);
    }

    @Test
    void testPonderateQuantity() {
        final int numberPeople = 3;
        Vector<Integer> previousQuantities = getQuantities();

        for (int i = 0; i < previousQuantities.size(); ++i){
            final int quantity = previousQuantities.get(i);
            final Integer newQuantity = (int) Math.ceil(quantity*1.0/testRecipe.getPeople()*numberPeople) ;
            previousQuantities.set(i, newQuantity);
        }
        testRecipe.ponderateQuantity(numberPeople);
        assert(testRecipe.getPeople() == numberPeople);
        assertEquals(previousQuantities, getQuantities());
    }

    private Vector<Integer> getQuantities() {
        Vector<Integer> previousQuantities = new Vector<>();
        for (IngredientRecipe ingredient : testRecipe.getIngredients()){
            previousQuantities.add(ingredient.getQuantity());
        }
        return previousQuantities;
    }
}