package com.g04autochef.data_access.filters.shoppingListFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.ShoppingList;

public abstract class ShoppingListField extends FilterField<ShoppingList> {
    protected ShoppingListField(String name, String value) throws FilterException {
        super(name, value);
    }
}
