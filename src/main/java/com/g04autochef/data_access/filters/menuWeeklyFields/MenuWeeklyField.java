package com.g04autochef.data_access.filters.menuWeeklyFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.MenuWeekly;

public abstract class MenuWeeklyField extends FilterField<MenuWeekly> {
    public MenuWeeklyField(String name, String value) throws FilterException {
        super(name, value);
    }
}
