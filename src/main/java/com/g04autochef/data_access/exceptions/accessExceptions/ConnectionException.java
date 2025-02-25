package com.g04autochef.data_access.exceptions.accessExceptions;

public final class ConnectionException extends AccessException{
    public ConnectionException(Throwable cause) {
        super("Couldn't connect to persistant storage", cause);
    }
}
