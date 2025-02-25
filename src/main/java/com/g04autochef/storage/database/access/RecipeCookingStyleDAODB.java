package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.ConnectionException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.recipeCookingStyleFields.RecipeCookingStyleFieldName;
import com.g04autochef.model.storableDAO.RecipeCookingStyle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

 public final class RecipeCookingStyleDAODB extends DAO_DB<RecipeCookingStyle> {
    public RecipeCookingStyleDAODB() throws ConnectionException {
    }

    @Override
    protected String getStringDelete() {
        return "DELETE from recipe_cooking_style where recipe_cooking_style_name=?";
    }

    @Override
    protected String getStringSelect() {
        return "SELECT * from recipe_cooking_style where ";
    }

    @Override
    protected String getStringSelectGroupBy() {
        return "";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT * from recipe_cooking_style";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT recipe_cooking_style_id FROM recipe_cooking_style where recipe_cooking_style_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "recipe_cooking_style_id";
    }

    @Override
    protected Filter<RecipeCookingStyle> makeBasicFilter(RecipeCookingStyle obj) {
        Filter<RecipeCookingStyle> filter = new Filter<>();
        try {
            filter.addField(new RecipeCookingStyleFieldName(obj.style()));
        } catch (FilterException e) {
            e.printStackTrace();
        }
        return filter;
    }

    @Override
    protected void insert(RecipeCookingStyle obj, Integer id) throws AccessException {
        if (isPresent(obj)) throw new DoubleInsertException();
        String insertIngredientType= "INSERT into recipe_cooking_style(recipe_cooking_style_id,recipe_cooking_style_name) values(?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
            if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,obj.style());
            stmt.executeUpdate();
        }
        catch (SQLException e){throw new AccessException(e);}
    }

    @Override
    RecipeCookingStyle buildModelObj(ResultSet rs) throws SQLException {
        return new RecipeCookingStyle(rs.getString("recipe_cooking_style_name"));
    }
}