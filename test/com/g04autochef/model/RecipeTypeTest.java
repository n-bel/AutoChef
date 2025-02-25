package com.g04autochef.model;

import com.g04autochef.model.storableDAO.RecipeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecipeTypeTest {

    @Test
    public void testRecipeTypeTypeNameEmpty(){
        final String type = "";
        Assertions.assertThrows(IllegalArgumentException.class,() -> new RecipeType(type));
    }

    @Test
    public void testRecipeTypeTypeNameNotEmpty(){
        final String type = "test";
        new RecipeType(type);
    }

}