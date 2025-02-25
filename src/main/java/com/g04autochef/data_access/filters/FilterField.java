package com.g04autochef.data_access.filters;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterValueNullException;
import com.g04autochef.model.storableDAO.StorableDAO;


/**
 * Field for a filter. The genericity is used to avoid incoherent filters and fields combinations
 */
public abstract class FilterField<Type extends StorableDAO> {
    private final String fieldName;
    private final String fieldValue;

    protected FilterField(String name, String value) throws FilterException {
        if (value.isBlank()) throw new FilterValueNullException();
        fieldName = name;
        fieldValue = value;
    }
    final public String getFieldName() {return fieldName;}
    final public String getFieldValue() {return fieldValue;}
}
