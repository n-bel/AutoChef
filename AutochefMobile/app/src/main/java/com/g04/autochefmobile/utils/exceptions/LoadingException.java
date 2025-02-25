package com.g04.autochefmobile.utils.exceptions;

public class LoadingException extends Exception {
    public LoadingException(Throwable cause){
        super("Exception lors du chargement de données", cause);
    }
}
