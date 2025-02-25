package com.g04autochef.data_access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.model.storableDAO.StorableDAO;

public interface Picturable<Type extends StorableDAO> {
    String getImagePath(Type t);

    void setImage(Type t, String path) throws AccessException;
}
