package com.g04autochef.model.storableDAO;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Shop
 * Has a unique name and address (for map),
 * contains ingredients with their associated price in this particular shop.
 */
public final class Shop implements StorableDAO {
    private final String name;
    private final String address;
    private final Vector<IngredientPrice> ingredients;

    public Shop(final String name, final String address) {
        if (name.isBlank()){
            throw new IllegalArgumentException("Shop name cannot be empty or blank");
        }
        if (address.isBlank()){
            throw new IllegalArgumentException("Shop adress cannot be empty or blank");
        }
        this.name = name;
        this.address = address;
        this.ingredients = new Vector<>();
    }

    public Shop(Shop shop, Vector<IngredientPrice> ingredients) {
        Vector<IngredientPrice> allIngredients = new Vector<>();
        allIngredients.addAll(ingredients);
        allIngredients.addAll(shop.getIngredients());
        this.name = shop.getName();
        this.address = shop.getAddress();
        this.ingredients = allIngredients;

    }

    public String getName(){
        return this.name;
    }
    public Vector<IngredientPrice> getIngredients(){
        return this.ingredients;
    }
    public String getAddress(){
        return this.address;
    }


    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }
}
