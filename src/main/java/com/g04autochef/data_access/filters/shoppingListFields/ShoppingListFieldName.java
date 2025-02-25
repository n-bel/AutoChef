package com.g04autochef.data_access.filters.shoppingListFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class ShoppingListFieldName extends ShoppingListField{
    public ShoppingListFieldName(String value) throws FilterException {
        super("shopping_list_name", value);
    }
}
