package com.g04.autochefmobile.model;


/**
 * Ingredient with quantity and selected unit.
 */
public final class IngredientWithQuantity {
    private final Ingredient ingredient;
    private final String chosenUnit;
    private Integer quantity;

    public IngredientWithQuantity(final Ingredient ingredient, final String chosenUnit, Integer quantity) {
        this.ingredient = new Ingredient(ingredient);
        this.chosenUnit = chosenUnit;
        this.quantity = quantity;
    }

    public String getUnit(){
        return chosenUnit;
    }

    public String getName(){
        return ingredient.getName();
    }

    public String getType(){
        return ingredient.getType();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {this.quantity = quantity;}

}
