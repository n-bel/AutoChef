package com.g04autochef.data_access.filters.recipeTypeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.RecipeType;

public abstract class RecipeTypeField extends FilterField<RecipeType> {
    protected RecipeTypeField(String name,String value) throws FilterException {
        super(name,value);
    }
}
