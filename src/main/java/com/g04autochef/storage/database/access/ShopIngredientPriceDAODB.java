package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ShopFields.ShopFieldName;
import com.g04autochef.model.storableDAO.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * ShopIngredientPriceDAODB
 * Retrieves the shop, it's ingredients,
 * the quantity of each ingredient and it's price.
 * Helps generate queries for the Data Base.
 */

final public class ShopIngredientPriceDAODB extends DAO_DB<Shop>{
    private final IngredientDAODB ingredientDAODB= new IngredientDAODB();
    private final ShopDAODB shopDAODB= new ShopDAODB();
    private final UnitDAODB unitDAODB= new UnitDAODB();

    public ShopIngredientPriceDAODB() throws AccessException {
    }

    @Override
    protected String getStringGetID() {
        return "SELECT shop_id FROM SHOP_INGREDIENT_PRICE NATURAL JOIN SHOP where shop_name=?";
    }

    @Override
    protected String getStringDelete() {
        return "DELETE FROM SHOP_INGREDIENT_PRICE WHERE shop_id in (SELECT shop_id FROM SHOP_INGREDIENT_PRICE  NATURAL JOIN SHOP s where s.shop_name=?)";
    }

    @Override
    protected String getStringSelect() {
        return "Select distinct shop_id FROM SHOP_INGREDIENT_PRICE NATURAL JOIN SHOP where";
    }

    @Override
    protected String getStringSelectAll() {
        return "Select shop_id FROM SHOP_INGREDIENT_PRICE";
    }

    @Override
    protected String getIDColumnName() {
        return "shop_id,ingredient_id,unit_id";
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
        if (isPresent(shop)) throw new DoubleInsertException();
        String insertIngredientType= "INSERT into SHOP_INGREDIENT_PRICE(shop_id, ingredient_id, unit_id, price, quantity) values(?,?,?,?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertIngredientType)){
            int shopid = shopDAODB.getID(shop);

            for (IngredientPrice ingredient : shop.getIngredients()) {
                int ingredientid = ingredientDAODB.getID(ingredient.getIngredient().getIngredient());
                int unitid = unitDAODB.getID(ingredient.getIngredient().getUnit());
                stmt.setInt(1, shopid);
                stmt.setInt(2, ingredientid);
                stmt.setInt(3, unitid);
                stmt.setDouble(4, ingredient.getPrice());
                stmt.setDouble(5, ingredient.getQuantity());
                stmt.executeUpdate();
            }
        }
        catch (SQLException e){throw new AccessException(e);}
    }

    @Override
    public Shop buildModelObj(ResultSet rs) throws SQLException {
        Shop shop = getShop(rs.getInt("shop_id"));
        Vector<IngredientPrice> ingredients = getIngredients(rs.getInt("shop_id"),shop.getName());
        return new Shop(shop, ingredients);
    }

    private Vector<IngredientPrice> getIngredients(Integer id,String shopname) {
        Vector<IngredientPrice> ingredients = new Vector<> ();
        String request = "SELECT ingredient_name,ingredient_type_name,unit_name,price,quantity " +
                "FROM INGREDIENT NATURAL JOIN SHOP_INGREDIENT_PRICE NATURAL JOIN INGREDIENT_TYPE NATURAL JOIN INGREDIENT_UNIT NATURAL JOIN UNIT WHERE shop_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,Integer.toString(id));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                double price = rs.getDouble("price");
                String unitname = rs.getString("unit_name");
                Integer quantity = rs.getInt("quantity");
                IngredientWithQuantity ingredient = new IngredientWithQuantity(ingredientDAODB.buildModelObj(rs),new Unit(unitname),quantity);
                IngredientPrice ingredientPrice = new IngredientPrice(price, shopname, ingredient);
                ingredients.add(ingredientPrice);}
        }
        catch (SQLException ignored){}
        return ingredients;
    }

    private Shop getShop(Integer id){
        String request = "SELECT *" +
                "FROM SHOP WHERE shop_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,Integer.toString(id));
            ResultSet rs = stmt.executeQuery();
            return new Shop(rs.getString("shop_name"),rs.getString("shop_address"));
        }
        catch (SQLException ignored){}
        return null;
    }
}
