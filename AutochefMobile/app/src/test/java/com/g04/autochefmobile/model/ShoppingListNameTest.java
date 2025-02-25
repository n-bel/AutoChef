package com.g04.autochefmobile.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ShoppingListNameTest {
    @Test
    public void isSelectedTest(){
        ShoppingListName shoppingListName = new ShoppingListName("ma super liste");
        shoppingListName.setSelected(true);
        assertTrue(shoppingListName.isSelected());
    }
}