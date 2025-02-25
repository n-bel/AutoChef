package com.g04autochef.data_access.filters.menuDailyFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.MenuDaily;

public abstract class MenuDailyField extends FilterField<MenuDaily> {

    protected MenuDailyField(String name, String value) throws FilterException {
        super(name, value);
    }
}
