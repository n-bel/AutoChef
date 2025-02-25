package com.g04autochef.data_access.filters.recipeTypeFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

final public class RecipeTypeFieldName extends RecipeTypeField{
    public RecipeTypeFieldName(String value) throws FilterException {
        super("recipe_type_name",value);
    }
}
