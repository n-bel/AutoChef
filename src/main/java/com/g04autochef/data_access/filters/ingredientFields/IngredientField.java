package com.g04autochef.data_access.filters.ingredientFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.Ingredient;

public abstract class IngredientField extends FilterField<Ingredient> {
    protected IngredientField(String name, String value) throws FilterException {
        super(name, value);
    }
}
