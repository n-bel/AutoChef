package com.g04.autochefmobile.componentView;

/**
 * Class that will contain the name and information of an ingredient and
 */
public class IngredientView implements DisplayableElement {
    private String info;
    private String name;
    private boolean selected;

    public IngredientView(String code, String name, boolean selected){
        this.info = code;
        this.name = name;
        this.selected = selected;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public String getInfo() {
        return info;
    }
}
