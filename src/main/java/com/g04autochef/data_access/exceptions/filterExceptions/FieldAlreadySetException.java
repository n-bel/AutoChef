package com.g04autochef.data_access.exceptions.filterExceptions;

public final class FieldAlreadySetException extends FilterException{
    public FieldAlreadySetException() {
        super("Field already set !!");
    }
}
