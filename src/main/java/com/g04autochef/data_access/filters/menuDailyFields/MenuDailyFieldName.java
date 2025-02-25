package com.g04autochef.data_access.filters.menuDailyFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class MenuDailyFieldName extends MenuDailyField{

    public MenuDailyFieldName(String value) throws FilterException {
        super("menu_name", value);
    }
}
