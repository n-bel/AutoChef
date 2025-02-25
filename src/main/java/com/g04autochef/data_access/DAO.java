package com.g04autochef.data_access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.StorableDAO;

import java.util.Vector;

/**
 * Needed to interact with a persistent storage, allow for a loose coupling
 * between the storage type and the controller
 * @param <Type>
 */
public abstract class DAO<Type extends StorableDAO> {
    /**
     * Exist to avoid the persistant storage to implement the filter's field with the same name as
     * "FilterField.name" (in most case just returning field.name will work)
     */
    protected abstract String translateFieldName(final FilterField<Type> field);

    /**
     * {@return all object <Type> present in the persistent storage}
     */
    public abstract Vector<Type> selectAll() throws AccessException;

    /**
     * Insert "obj" in the persistent storage
     * @param obj
     */
    public abstract void insert(final Type obj) throws AccessException;

    /**
     * Delete "obj" from the persistent storage
     * @param obj
     */
    public abstract void delete(final Type obj) throws AccessException;

    /**
     * {@return Vector containing all object in the persistent storage matching each field set in the filter}
     */
    public abstract Vector<Type> select(final Filter<Type> filter) throws AccessException;

}
