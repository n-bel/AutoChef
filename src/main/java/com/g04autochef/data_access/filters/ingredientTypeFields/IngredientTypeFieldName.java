package com.g04autochef.data_access.filters.ingredientTypeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;


public final class IngredientTypeFieldName extends IngredientTypeField {
    public IngredientTypeFieldName(String value) throws FilterException {
        super("ingredient_type_name", value);
    }
}
