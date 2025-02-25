package com.g04.autochefmobile.componentView;

public class IngredientTypeView implements DisplayableElement {
    private final String name;

    public IngredientTypeView(String name) {
        this.name = name;
    }

    @Override
    public String getName() {return name;}

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selected) {}

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getInfo() {
        return "";
    }
}