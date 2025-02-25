package com.g04autochef.data_access.filters.archiveFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.Archive;

/**
 * !! All archivable class should have the same field name to indicate
 * if it is archived or not in the persistent storage in order for this
 * filter to work on all of those types !!
 *
 *
 * @param <Type>
 */
public abstract class ArchiveField<Type extends Archive> extends FilterField<Type> {
    protected ArchiveField(String name, String value) throws FilterException {
        super(name, value);
    }
}
