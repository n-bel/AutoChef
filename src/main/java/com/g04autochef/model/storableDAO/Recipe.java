package com.g04autochef.model.storableDAO;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Recipe
 * Has instructions, type and style,
 * ingredients with quantities based on the number of people.
 */
public final class Recipe implements StorableDAO {
    private final Vector<IngredientRecipe> ingredients;
    private final Vector<String> instructions;
    private Integer people;
    private final String name;
    private final RecipeType type;
    private final RecipeCookingStyle cookingStyle;

    public Recipe(Vector<IngredientRecipe> ingredients, Vector<String> instructions,
                  Integer people, String name, RecipeType type) {
        this(ingredients, instructions, people, name, type, null);
    }

    public Recipe(final Vector<IngredientRecipe> ingredients, final Vector<String> instructions,
                  final Integer people, final String name, final RecipeType type, final RecipeCookingStyle style) {
        this.ingredients = ingredients;
        for (IngredientRecipe e : this.ingredients) { e.setUsedInRecipe(this); }
        this.instructions = instructions;
        this.people = people;
        this.name = name;
        this.type = type;
        this.cookingStyle = style;
    }

    public void ponderateQuantity(int nbPeople) {
        for (IngredientRecipe ingr : ingredients) {
            int newQuantity = (int) Math.ceil(ingr.getQuantity()*1.0/this.people*nbPeople);
            ingr.setQuantity(newQuantity);
        }
        this.people = nbPeople;
    }

    public Vector<IngredientRecipe> getIngredients(){
        return ingredients;
    }

    public Vector<IngredientWithQuantity> getIngredientsWithQuantites() {
        final Vector<IngredientWithQuantity> ingredientWithQuantities = new Vector<>();
        for (final IngredientRecipe ingredientRecipe : ingredients){
            ingredientWithQuantities.add(ingredientRecipe.getIngredient());
        }
        return ingredientWithQuantities;
    }

    public Vector<String> getInstructions() {
        return instructions;
    }
    public Integer getPeople() {
        return people;
    }
    public String getName() {
        return name;
    }
    public RecipeType getType() {
        return type;
    }
    public RecipeCookingStyle getCookingStyle() {
        return cookingStyle;
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }

}
