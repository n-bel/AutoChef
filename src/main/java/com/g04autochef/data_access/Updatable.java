package com.g04autochef.data_access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.model.storableDAO.StorableDAO;


/**
 * Allow an object in the persistent storage to be updated
 * @param <Type>
 */
public interface Updatable<Type extends StorableDAO> {
    void update(Type t) throws AccessException;

}
