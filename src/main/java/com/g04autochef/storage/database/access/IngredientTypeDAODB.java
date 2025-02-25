package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ingredientTypeFields.IngredientTypeFieldName;
import com.g04autochef.model.storableDAO.IngredientType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class IngredientTypeDAODB extends DAO_DB<IngredientType> {

    public IngredientTypeDAODB() throws AccessException {}

    @Override
    protected String getStringSelect() {return "SELECT ingredient_type_name FROM INGREDIENT_TYPE where";}

    @Override
    protected String getStringSelectAll() {return "SELECT ingredient_type_name FROM INGREDIENT_TYPE";}

    @Override
    protected String getStringGetID() {
        return "SELECT ingredient_type_id FROM INGREDIENT_TYPE where ingredient_type_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "ingredient_type_id";
    }

    @Override
    IngredientType buildModelObj(final ResultSet rs) throws SQLException {
        return new IngredientType(rs.getString("ingredient_type_name"));
    }

    @Override
    protected Filter<IngredientType> makeBasicFilter(IngredientType obj){
        Filter<IngredientType> filter=new Filter<>();
        try{filter.addField(new IngredientTypeFieldName(obj.type()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}

    @Override
    protected void insert(IngredientType obj,Integer id) throws AccessException {
        if (isPresent(obj)) throw new DoubleInsertException();
        String insertIngredientType= "INSERT into INGREDIENT_TYPE(ingredient_type_id,ingredient_type_name) values(?,?)";
        try(PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
            if (id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,obj.type());
            stmt.executeUpdate();
        }
        catch (SQLException e){throw new AccessException(e);}

    }



}



