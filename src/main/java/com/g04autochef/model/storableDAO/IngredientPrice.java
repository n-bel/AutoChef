package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * IngredientPrice
 * Has a price for it's ingredient.
 */

public final class IngredientPrice implements StorableDAO {
    private final IngredientWithQuantity ingredient;
    private final double price;
    private final String shopName;

    public IngredientPrice(double price, final String name, final IngredientWithQuantity ingredient) {
        this.price = price;
        this.ingredient = ingredient;
        this.shopName = name;
    }

    public String getShopName(){
        return this.shopName;
    }

    public IngredientWithQuantity getIngredient(){
        return this.ingredient;
    }

    public Double getPrice(){
        return this.price;
    }

    public double getQuantity() {
        return this.ingredient.getQuantity();
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(this.getShopName());
        return res;
    }
}
