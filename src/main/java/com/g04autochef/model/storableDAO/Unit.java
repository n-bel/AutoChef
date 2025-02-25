package com.g04autochef.model.storableDAO;

import java.util.ArrayList;

/**
 * Unit type for Ingredients.
 */
public final class Unit implements StorableDAO {

    private final String name;

    public Unit(String name){
        this.name= name;
    }

    public Unit(final Unit unit){
        this.name = unit.getName();
    }

    public String getName() {return name; }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }
}