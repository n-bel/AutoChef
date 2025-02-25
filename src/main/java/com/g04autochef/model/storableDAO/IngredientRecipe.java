package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Ingredient used in a recipe.
 */
final public class IngredientRecipe implements StorableDAO {

    private final IngredientWithQuantity ingredientWithQuantity;
    private Recipe inRecipe;

    public IngredientRecipe(final Ingredient ingredient, final Unit chosenUnit, Integer quantity){
        this.ingredientWithQuantity = new IngredientWithQuantity(ingredient, chosenUnit, quantity);
    }

    public IngredientRecipe(final IngredientWithQuantity ingredientWithQuantity){
        this.ingredientWithQuantity = new IngredientWithQuantity(ingredientWithQuantity);
    }

    public IngredientWithQuantity getIngredient(){ return ingredientWithQuantity;}

    public String getUnitName() {return ingredientWithQuantity.getUnitName();}

    public String getName() {return ingredientWithQuantity.getName();}

    public Unit getUnit(){return ingredientWithQuantity.getUnit();}

    public String getType() {return ingredientWithQuantity.getType();}

    public Integer getQuantity() {return ingredientWithQuantity.getQuantity();}

    public void setQuantity(int quantity) { this.ingredientWithQuantity.setQuantity(quantity);}

    void setUsedInRecipe(final Recipe recipe) {inRecipe = recipe;}

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(ingredientWithQuantity.getName());
        res.add(inRecipe.getName());
        return res;
    }
}
