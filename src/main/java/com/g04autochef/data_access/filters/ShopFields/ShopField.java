package com.g04autochef.data_access.filters.ShopFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.Shop;

public abstract class ShopField extends FilterField<Shop> {
    protected ShopField(String name, String value) throws FilterException {
        super(name, value);
    }
}