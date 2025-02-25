package com.g04autochef.model.storableDAO;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Ingredient
 * Has type and multiple possible units.
 */
public final class Ingredient implements StorableDAO {
    private final String name;
    private final IngredientType type;
    private final Vector<Unit> units;


    public Ingredient(final String name, final IngredientType type, final Vector<Unit> units){
        this.name = name;
        this.type = type;
        this.units = units;
    }

    public Ingredient(final Ingredient ingredient){
       this.name = ingredient.getName();
       this.type = ingredient.getType();
       this.units = ingredient.getUnits();
    }

    public String getName() { return name; }

    public IngredientType getType() { return type;}

    public Vector<Unit> getUnits() {
        return units;
    }

    public Vector<String> getUnitNames(){
        Vector<String> names = new Vector<>();
        for (Unit unit : units) {names.add(unit.getName());}
        return names;
    }

    @Override
    public ArrayList<String> getUniqueID() {
        ArrayList<String> res = new ArrayList<>();
        res.add(getName());
        return res;
    }

}


