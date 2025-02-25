package com.g04autochef.view.tableConstructors;

import java.util.Vector;

/**
 * Class that will contain the getAttributes method for the IngredientPriceFX
 */
public class IngredientPriceTableViewConstructor extends IngredientTableViewConstructor {
    @Override
    public Vector<String> getAttributes() {
        Vector<String> attributeVector = super.getAttributes();
        attributeVector.add("price");
        return attributeVector;
    }
}
