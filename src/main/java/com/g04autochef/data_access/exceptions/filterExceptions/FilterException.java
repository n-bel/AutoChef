package com.g04autochef.data_access.exceptions.filterExceptions;

public class FilterException extends Exception{
    protected FilterException(String errorMsg) {super(errorMsg);}
    public FilterException() {
        super("Unexpected filter exception occurred !!");
    }
}
