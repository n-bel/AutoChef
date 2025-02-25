package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.model.storableDAO.IngredientType;
import com.g04autochef.model.storableDAO.Unit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public final class IngredientDAODB extends DAO_DB<Ingredient> {
    private final IngredientTypeDAODB ingredientTypeDAO= new IngredientTypeDAODB();
    private final UnitDAODB unitDAO = new UnitDAODB();


    public IngredientDAODB() throws AccessException {}

    @Override
    protected String getStringDelete() {
        return "DELETE FROM INGREDIENT i where i.ingredient_name=? ";
    }

    @Override
    protected String getStringSelect() {
        return "select ingredient_name,ingredient_type_name from ((INGREDIENT  natural join INGREDIENT_TYPE) natural join " +
                "(INGREDIENT_UNIT natural join UNIT)) where";
    }

    @Override
    protected String getStringSelectGroupBy() {
        return "GROUP BY ingredient_name";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT * FROM INGREDIENT natural join INGREDIENT_TYPE";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT ingredient_id FROM INGREDIENT where ingredient_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "ingredient_id";
    }

    @Override
    protected Filter<Ingredient> makeBasicFilter(final Ingredient ingredient){
        Filter<Ingredient> filter=new Filter<>();
        try{
            filter.addField(new IngredientFieldName(ingredient.getName()));
        }
        catch(FilterException e){e.printStackTrace();}
        return filter;
    }
    @Override
    public Ingredient buildModelObj(final ResultSet rs) throws SQLException {
        String name = rs.getString("ingredient_name");
        String type= rs.getString("ingredient_type_name");
        Vector <Unit> units = getIngredientUnits(name);
        return new Ingredient(name,new IngredientType(type),units);
    }

    public Vector<Unit> getIngredientUnits(String name) {
        Vector<Unit> units = new Vector<>();
        String request = "select * from((select * from INGREDIENT i where i.ingredient_name= ?) " +
                "natural join ingredient_unit natural join unit) ";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                units.add(new Unit(rs.getString("unit_name")));
            }
        }
        catch (SQLException ignored){}
        return units;
    }

    @Override
    protected void insert(final Ingredient ingredient,Integer id) throws AccessException{
        if (isPresent(ingredient)) throw new DoubleInsertException();
        String insertIngredient = "INSERT into INGREDIENT(ingredient_id,ingredient_name,ingredient_type_id) values(?,?,?)";
        String insertIngredientUnit= "INSERT into INGREDIENT_UNIT(ingredient_id,unit_id) values(?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertIngredient);
             PreparedStatement stmt2 = getPreparedStatement(insertIngredientUnit)){
            int typeId = ingredientTypeDAO.getID(ingredient.getType());

            if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,ingredient.getName());
            stmt.setInt(3,typeId);
            stmt.executeUpdate();

            int unit_id;
            int ingredient_id = getID(ingredient);
            for (Unit unit: ingredient.getUnits()){
                unit_id = unitDAO.getID(unit);
                stmt2.setInt(1,ingredient_id);
                stmt2.setInt(2,unit_id);
                stmt2.addBatch();
            }
            stmt2.executeBatch();
        }
        catch (SQLException e){throw new AccessException(e);}
    }
}
