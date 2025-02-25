package com.g04autochef.data_access.filters.recipeCookingStyleFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.RecipeCookingStyle;

public abstract class RecipeCookingStyleField extends FilterField<RecipeCookingStyle> {
    protected RecipeCookingStyleField(String name, String value) throws FilterException {
        super(name, value);
    }
}
