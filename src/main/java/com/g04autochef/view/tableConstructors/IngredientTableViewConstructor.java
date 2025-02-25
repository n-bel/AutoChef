package com.g04autochef.view.tableConstructors;

import java.util.Vector;

/**
 * Class that will contain the getAttributes method for the IngredientRecipeFX
 */
public class IngredientTableViewConstructor implements TableViewable.TableViewListener {
    @Override
    public Vector<String> getAttributes() {
        Vector<String> attributeVector = new Vector<>();
        attributeVector.add("name");
        attributeVector.add("type");
        attributeVector.add("quantity");
        attributeVector.add("unitName");
        attributeVector.add("delete");
        return attributeVector;
    }
}