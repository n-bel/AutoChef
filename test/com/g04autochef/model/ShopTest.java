package com.g04autochef.model;

import com.g04autochef.model.storableDAO.Shop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShopTest {

    @Test
    public void testShopNameEmpty(){
        final String name = "  ";
        final String address = "stuff";
        Assertions.assertThrows(IllegalArgumentException.class,() -> new Shop(name, address));
    }

    @Test
    public void testShopAddressEmpty(){
        final String name = "stdsdfj";
        final String address = "  ";
        Assertions.assertThrows(IllegalArgumentException.class,() -> new Shop(name, address));
    }

    @Test
    public void testShop(){
        final String name = "csts  ";
        final String address = "stuff";
        new Shop(name, address);
    }


}