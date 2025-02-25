package com.g04autochef.data_access.filters.unitFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.Unit;

public abstract class UnitField extends FilterField<Unit> {
    protected UnitField(String name, String value) throws FilterException {
        super(name, value);
    }
}
