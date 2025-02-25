package com.g04autochef.data_access.filters.unitFields;


import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;

final public class UnitFieldName extends UnitField {
    public UnitFieldName(String value) throws FilterException {
        super("unit_name",value);
    }
}
