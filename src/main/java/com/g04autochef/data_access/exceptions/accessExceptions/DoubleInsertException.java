package com.g04autochef.data_access.exceptions.accessExceptions;

public final class DoubleInsertException extends AccessException {
    public DoubleInsertException(Throwable cause) {
        super("Element already present in storage", cause);
    }
    public DoubleInsertException() {
        super("Element already present in storage");
    }
}
