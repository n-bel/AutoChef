package com.g04autochef.data_access.filters.ingredientTypeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.IngredientType;

public abstract class IngredientTypeField extends FilterField<IngredientType> {
    protected IngredientTypeField(String name, String value) throws FilterException {
        super(name, value);
    }
}
