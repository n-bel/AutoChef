package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Type of an ingredient.
 * Ex: Légume, viande, féculent
 */
public record IngredientType(String type) implements StorableDAO {

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(type);
        return res;
    }


}
