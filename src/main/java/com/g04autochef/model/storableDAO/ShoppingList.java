package com.g04autochef.model.storableDAO;

import java.util.*;

/**
 * Shopping list.
 * Can be archived.
 */
public final class ShoppingList extends Archive {
    private final String name;
    private Vector<IngredientRecipe> ingredients = new Vector<>();

    public ShoppingList(final String name, Vector<IngredientRecipe> ingredients) {
        super();
        if (name.isBlank()){
            throw new IllegalArgumentException("Shopping list name cannot be empty");
        }
        this.name = name;
        this.addAllIngredients(ingredients);
    }

    public String getName() {
        return name;
    }
    public Vector<IngredientRecipe> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Vector<IngredientRecipe> newIngredients) {
        ingredients =  newIngredients;
    }

    /**
     * Adds ingredient to recipe.
     * If an ingredient with the same name already exists : adds their quantities together,
     * otherwise adds a new ingredient into the recipe.
     */
    public void addIngredient(final IngredientRecipe newIngredientRecipe) {
        boolean isAddedToExisting = false;
        for (final IngredientRecipe ingredientRecipe: this.ingredients){
            boolean sameIngredientName = newIngredientRecipe.getName().equals(ingredientRecipe.getName());
            if (sameIngredientName){
                int newTotalQuantity = ingredientRecipe.getQuantity()+newIngredientRecipe.getQuantity();
                ingredientRecipe.setQuantity(newTotalQuantity);
                isAddedToExisting = true;
            }
        }
        if (!isAddedToExisting)
            this.ingredients.add(newIngredientRecipe);
    }

    public void addAllIngredients(final Collection<IngredientRecipe> ingredientsToAdd){
        for (IngredientRecipe ingredient : ingredientsToAdd) {
            addIngredient(ingredient);
        }
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }

    /**
     * Sort ingredients by type and unit
     * @return Map Type : (Map Unit : Vector(IngredientRecipe))
     */
    public HashMap<String, HashMap<String, Vector<IngredientRecipe>>> getSortedIngredients() {
        HashMap<String, HashMap<String, Vector<IngredientRecipe>>> sortedIngredients = new HashMap<>();
        for (IngredientRecipe ingr : this.ingredients) {
            if (!sortedIngredients.containsKey(ingr.getType())) {
                sortedIngredients.put(ingr.getType(), new HashMap<>());
            }
            if (!sortedIngredients.get(ingr.getType()).containsKey(ingr.getUnit().getName())) {
                sortedIngredients.get(ingr.getType()).put(ingr.getUnit().getName(), new Vector<>());
            }
            sortedIngredients.get(ingr.getType()).get(ingr.getUnit().getName()).add(ingr);
        }
        return sortedIngredients;
    }
}
