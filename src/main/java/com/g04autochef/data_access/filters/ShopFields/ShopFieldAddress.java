package com.g04autochef.data_access.filters.ShopFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.ShopFields.ShopField;

public final class ShopFieldAddress extends ShopField {
    public ShopFieldAddress(String value) throws FilterException {
        super("shop_address", value);
    }
}