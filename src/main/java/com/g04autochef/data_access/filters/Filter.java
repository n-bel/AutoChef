package com.g04autochef.data_access.filters;

import com.g04autochef.data_access.exceptions.filterExceptions.FieldAlreadySetException;
import com.g04autochef.model.storableDAO.StorableDAO;

import java.util.Vector;

/**
 * Used by DAO<Type>.select in order to return the object with matching fields
 * The filter only can have one value for each field !!
 * @param <Type>
 */
public final class Filter<Type extends StorableDAO> {

    private final Vector<FilterField<Type>> fields = new Vector<>();

    public Filter() {}

    /**
     * Add a field and his value to the filter
     * @param field
     */
     public void addField(final FilterField<Type> field) throws FieldAlreadySetException {
        for (FilterField<Type> f : fields) {
            if (f.getClass().getName().equals(field.getClass().getName()))
                throw new FieldAlreadySetException();
        }
        fields.add(field);
    }
    public  Vector<FilterField<Type>> getFields() {return fields;}
}


