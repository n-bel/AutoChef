package com.g04autochef.data_access.exceptions.accessExceptions;

public final class UpdateNotPresentException extends AccessException {
    public UpdateNotPresentException(Throwable cause) {
        super("No matching Element in storage", cause);
    }
}
