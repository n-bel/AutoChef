package com.g04autochef.data_access.filters.archiveFields;

import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.Archive;

public final class ArchiveArchivedField<Type extends Archive> extends ArchiveField<Type> {
    public ArchiveArchivedField() throws FilterException {
        super("archive", "1");
    }
}
