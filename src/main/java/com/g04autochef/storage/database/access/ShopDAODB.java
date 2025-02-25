package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ShopFields.ShopFieldName;
import com.g04autochef.model.storableDAO.Shop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ShopDAODB
 * Retrieves the name of the shop and it's address.
 * Helps generate queries for the Data Base.
 */

public final class ShopDAODB extends DAO_DB<Shop> {

    public ShopDAODB() throws AccessException {}

    @Override
    protected String getStringGetID() {
        return "SELECT shop_id FROM SHOP where shop_name=?";
    }

    @Override
    protected String getStringDelete() {
        return "DELETE FROM SHOP where shop_name=?";
    }

    @Override
    protected String getStringSelect() {
        return "Select * FROM SHOP where";
    }

    @Override
    protected String getStringSelectAll() {
        return "Select * FROM SHOP";
    }

    @Override
    protected String getIDColumnName() {
        return "shop_id";
    }

    @Override
    protected Filter<Shop> makeBasicFilter(Shop obj) {
        Filter<Shop> filter=new Filter<>();
        try{filter.addField(new ShopFieldName(obj.getName()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;
    }

    @Override
    protected void insert(Shop shop, Integer id) throws AccessException {
        if (isPresent(shop)) {throw new DoubleInsertException();}
        else{
            String insertIngredientType= "INSERT into SHOP(shop_id, shop_name, shop_address) values(?,?,?)";
            try (PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
                if(id==null) stmt.setNull(1,java.sql.Types.INTEGER);
                else stmt.setInt(1,id);
                stmt.setString(2,shop.getName());
                stmt.setString(3,shop.getAddress());
                stmt.executeUpdate();
            }
            catch (SQLException e){throw new AccessException(e);}
        }
    }

    @Override
    public Shop buildModelObj(ResultSet rs) throws SQLException {
        String address=rs.getString("shop_address");
        String name=rs.getString("shop_name");
        return new Shop(name,address);
    }

}