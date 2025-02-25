package com.g04autochef.data_access.filters.recipeCookingStyleFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class RecipeCookingStyleFieldName extends RecipeCookingStyleField{
    public RecipeCookingStyleFieldName(String value) throws FilterException {
        super("recipe_cooking_style_name", value);
    }
}
