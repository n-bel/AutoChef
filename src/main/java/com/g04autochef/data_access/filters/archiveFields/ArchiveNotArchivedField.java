package com.g04autochef.data_access.filters.archiveFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.Archive;

public final class ArchiveNotArchivedField<Type extends Archive> extends ArchiveField<Type>{
    public ArchiveNotArchivedField() throws FilterException {
        super("archive", "0");
    }
}
