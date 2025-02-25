package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Ingredient with quantity and selected unit.
 */
public final class IngredientWithQuantity implements StorableDAO {
    private final Ingredient ingredient;
    private final Unit chosenUnit;
    private Integer quantity;

    public IngredientWithQuantity(final Ingredient ingredient, final Unit chosenUnit, Integer quantity) {
        this.ingredient = new Ingredient(ingredient);
        this.chosenUnit = new Unit(chosenUnit);
        this.quantity = quantity;
    }

    public IngredientWithQuantity(final IngredientWithQuantity ingredientWithQuantity){
        this.ingredient = new Ingredient(ingredientWithQuantity.getIngredient());
        this.chosenUnit = new Unit(ingredientWithQuantity.getUnit());
        this.quantity = ingredientWithQuantity.getQuantity();
    }

    public String getUnitName() {
        return chosenUnit.getName();
    }

    public Unit getUnit(){
        return chosenUnit;
    }

    public String getName(){
        return ingredient.getName();
    }

    public String getType(){
        return ingredient.getType().type();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {this.quantity = quantity;}

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(ingredient.getName());
        return res;
    }

}
