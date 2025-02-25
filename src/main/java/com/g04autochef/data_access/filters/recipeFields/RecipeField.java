package com.g04autochef.data_access.filters.recipeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.Recipe;

public abstract class RecipeField extends FilterField<Recipe> {
    protected RecipeField(String name, String value) throws FilterException {
        super(name, value);
    }
}
