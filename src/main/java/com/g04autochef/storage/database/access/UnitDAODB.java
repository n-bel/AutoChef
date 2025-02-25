package com.g04autochef.storage.database.access;


import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.unitFields.UnitFieldName;
import com.g04autochef.model.storableDAO.Unit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UnitDAODB extends DAO_DB<Unit> {

    public UnitDAODB() throws AccessException {
    }

    protected Unit buildModelObj(final ResultSet rs) throws SQLException {
        return new Unit(rs.getString("unit_name"));
    }

    @Override
    protected String getStringSelect() {return "SELECT unit_name FROM unit where";}

    @Override
    protected String getStringSelectAll() {
        return "SELECT unit_name FROM unit";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT unit_id FROM UNIT where unit_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "unit_id";
    }

    @Override
    protected Filter<Unit> makeBasicFilter(Unit obj){
        Filter<Unit> filter=new Filter<>();
        try{filter.addField(new UnitFieldName(obj.getName()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}

    @Override
    protected void insert(Unit unit,Integer id) throws AccessException{
        if (isPresent(unit)) throw new DoubleInsertException();
        String insertIngredientType= "INSERT into UNIT(unit_id,unit_name) values(?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
            if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,unit.getName());
            stmt.executeUpdate();
        }
        catch (SQLException e){throw new AccessException(e);}
    }


}


