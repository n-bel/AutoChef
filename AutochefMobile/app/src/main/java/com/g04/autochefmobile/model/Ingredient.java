package com.g04.autochefmobile.model;


/**
 * Ingredient
 * Has a type.
 */
public final class Ingredient {
    private final String name;
    private final String type;


    public Ingredient(final String name, final String type){
        this.name = name;
        this.type = type;
    }

    public Ingredient(final Ingredient ingredient){
        this.name = ingredient.getName();
        this.type = ingredient.getType();
    }

    public String getName() { return name; }

    public String getType() { return type;}

}