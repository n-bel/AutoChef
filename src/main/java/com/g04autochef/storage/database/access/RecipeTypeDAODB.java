package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.recipeTypeFields.RecipeTypeFieldName;
import com.g04autochef.model.storableDAO.RecipeType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class RecipeTypeDAODB extends DAO_DB<RecipeType> {
    public RecipeTypeDAODB() throws AccessException {
    }

    @Override
    protected String getStringSelect() {return "SELECT recipe_type_name FROM recipe_TYPE where";}

    @Override
    protected String getStringSelectAll() {return "SELECT recipe_type_name FROM recipe_TYPE";}

    @Override
    protected String getStringGetID() {
        return "SELECT recipe_type_id FROM recipe_type where recipe_type_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "recipe_type_id";
    }

    protected RecipeType buildModelObj(final ResultSet rs) throws SQLException {
        return new RecipeType(rs.getString("recipe_type_name"));
    }
    @Override
    protected Filter<RecipeType> makeBasicFilter(RecipeType obj){
        Filter<RecipeType> filter=new Filter<>();
        try{filter.addField(new RecipeTypeFieldName(obj.getType()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}


    @Override
    protected void insert(RecipeType type,Integer id) throws AccessException {
        if(isPresent(type)) throw new DoubleInsertException();
        String insertIngredientType= "INSERT into RECIPE_TYPE(recipe_type_id,recipe_type_name) values(?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
            if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,type.getType());
            stmt.executeUpdate();
        }
        catch (SQLException e) {throw new AccessException(e);}
    }


}
