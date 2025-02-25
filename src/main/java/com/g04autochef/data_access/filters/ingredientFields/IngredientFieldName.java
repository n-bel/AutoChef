package com.g04autochef.data_access.filters.ingredientFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class IngredientFieldName extends IngredientField{
    public IngredientFieldName(String value) throws FilterException {
        super("ingredient_name", value);
    }
}
