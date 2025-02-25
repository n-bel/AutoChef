package com.g04autochef.data_access.exceptions.accessExceptions;

public final class DeleteNotPresentException extends AccessException{
    public DeleteNotPresentException(Throwable cause) {super("Element not present for delete", cause);}
    public DeleteNotPresentException() {super("Element not present for delete");}
}
