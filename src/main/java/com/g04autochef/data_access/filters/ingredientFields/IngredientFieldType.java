package com.g04autochef.data_access.filters.ingredientFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class IngredientFieldType extends IngredientField {
    public IngredientFieldType(String value) throws FilterException {
        super("ingredient_type_name", value);
    }
}
