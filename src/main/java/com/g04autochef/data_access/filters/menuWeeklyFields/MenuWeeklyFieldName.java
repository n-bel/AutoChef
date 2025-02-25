package com.g04autochef.data_access.filters.menuWeeklyFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

public final class MenuWeeklyFieldName extends MenuWeeklyField{
    public MenuWeeklyFieldName(String value) throws FilterException {
        super("week_list_name", value);
    }
}
