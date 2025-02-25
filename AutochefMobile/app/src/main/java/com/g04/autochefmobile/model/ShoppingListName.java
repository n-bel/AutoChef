package com.g04.autochefmobile.model;

/**
 * Names shopping lists
 * Has a boolean for knowing if we selected it.
 */
public class ShoppingListName {
    private boolean selected;
    private final String name;

    public ShoppingListName(String name) {
        this.name = name;
        this.selected = false;
    }

    public String getName(){
        return this.name;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean b){
        this.selected = b;
    }
}
