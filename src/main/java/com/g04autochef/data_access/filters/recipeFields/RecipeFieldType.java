package com.g04autochef.data_access.filters.recipeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class RecipeFieldType extends RecipeField{
    public RecipeFieldType(String value) throws FilterException {
        super("recipe_type_name", value);
    }
}
