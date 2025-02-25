package com.g04autochef.data_access.filters.recipeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

final public class RecipeFieldStyle extends RecipeField{
    public RecipeFieldStyle(String value) throws FilterException {
        super("recipe_cooking_style_name", value);
    }
}
