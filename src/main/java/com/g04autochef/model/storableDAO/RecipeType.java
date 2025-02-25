package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Recipe type.
 * Ex: Végétarian, Poisson, Viande
 */
public final class RecipeType implements StorableDAO {
    private final String type;

    public RecipeType(final String type) {
        if (type.isBlank()){
            throw new IllegalArgumentException("Daily menu name cannot be empty");
        }
        this.type = type;
    }

    public String getType() {return type;}

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getType());
        return res;
    }

}
