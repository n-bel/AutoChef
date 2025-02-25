package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Recipe cooking style.
 * Ex: mijoté
 */
@SuppressWarnings("unused")
public record RecipeCookingStyle(String style) implements StorableDAO {

    public RecipeCookingStyle {
        if (style.isBlank()){
            throw new IllegalArgumentException("Cooking style name cannot be empty or blank");
        }
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(style);
        return res;
    }
}
