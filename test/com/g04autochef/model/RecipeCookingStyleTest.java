package com.g04autochef.model;


import com.g04autochef.model.storableDAO.RecipeCookingStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecipeCookingStyleTest {

    @Test
    public void testRecipeCookingStyleStyleNameEmpty(){
        final String style = "  ";
        Assertions.assertThrows(IllegalArgumentException.class,() -> new RecipeCookingStyle(style));
    }

    @Test
    public void testRecipeCookingStyleStyleNameNotEmpty(){
        final String style = "esttes";
        new RecipeCookingStyle(style);
    }
}