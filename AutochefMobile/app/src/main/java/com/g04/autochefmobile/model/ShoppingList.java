package com.g04.autochefmobile.model;

import java.util.Collection;
import java.util.Vector;

/**
 * Shopping list
 * Has a vector of ingredients.
 */
public class ShoppingList {
    private final String name;
    private final Vector<IngredientWithQuantity> ingredients = new Vector<>();

    public ShoppingList(final String name, Vector<IngredientWithQuantity> ingredients) {
        this.name = name;
        this.addAllIngredients(ingredients);
    }

    public String getName() {
        return name;
    }

    public Vector<IngredientWithQuantity> getIngredients() {
        return ingredients;
    }

    /**
     * Adds ingredient to recipe.
     * If an ingredient with the same name already exists : adds their quantities together,
     * otherwise adds a new ingredient into the recipe.
     */
    public void addIngredient(final IngredientWithQuantity newIngredientRecipe) {
        boolean isAddedToExisting = false;
        for (final IngredientWithQuantity ingredientQuantity: this.ingredients){
            boolean sameIngredientName = newIngredientRecipe.getName().equals(ingredientQuantity.getName());
            if (sameIngredientName){
                int newTotalQuantity = ingredientQuantity.getQuantity()+newIngredientRecipe.getQuantity();
                ingredientQuantity.setQuantity(newTotalQuantity);
                isAddedToExisting = true;
            }
        }
        if (!isAddedToExisting)
            this.ingredients.add(newIngredientRecipe);
    }

    public void addAllIngredients(final Collection<IngredientWithQuantity> ingredientsToAdd){
        for (IngredientWithQuantity ingredient : ingredientsToAdd) {
            addIngredient(ingredient);
        }
    }
}
