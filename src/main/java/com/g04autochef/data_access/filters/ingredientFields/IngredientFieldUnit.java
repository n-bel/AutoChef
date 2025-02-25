package com.g04autochef.data_access.filters.ingredientFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class IngredientFieldUnit extends IngredientField{
    public IngredientFieldUnit(String value) throws FilterException {
        super("unit_name", value);
    }
}
