package com.g04autochef.data_access.exceptions.accessExceptions;

public class AccessException extends Exception{
    protected AccessException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
    }
    protected AccessException(String errorMsg) {
        super(errorMsg);
    }

    public AccessException(Throwable cause) {
        super("Unexpected access exception occurred !!", cause);
    }
}
