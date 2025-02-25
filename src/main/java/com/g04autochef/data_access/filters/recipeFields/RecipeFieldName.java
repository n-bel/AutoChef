package com.g04autochef.data_access.filters.recipeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class RecipeFieldName extends RecipeField{
    public RecipeFieldName(String value) throws FilterException {
        super("recipe_name", value);
    }
}
