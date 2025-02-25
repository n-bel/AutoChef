package com.g04autochef.view.tableConstructors;

import java.util.Vector;

/**
 * Class that will contain the getAttributes method for the Recipe
 */
public class RecipeTableViewConstructor implements TableViewable.TableViewListener {
    @Override
    public Vector<String> getAttributes() {
        Vector<String> attributeVector = new Vector<>();
        attributeVector.add("name");
        return attributeVector;
    }
}
