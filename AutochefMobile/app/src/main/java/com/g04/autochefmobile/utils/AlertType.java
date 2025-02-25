package com.g04.autochefmobile.utils;

public enum AlertType {
    ERROR("Error"),
    INFO("Info"),
    ;

    private final String title;

    AlertType(final String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
