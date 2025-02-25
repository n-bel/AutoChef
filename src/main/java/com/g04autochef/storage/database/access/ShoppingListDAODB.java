package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.Updatable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.ConnectionException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.shoppingListFields.ShoppingListFieldName;
import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.g04autochef.model.storableDAO.Unit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public final class ShoppingListDAODB extends DAO_DB<ShoppingList> implements Updatable<ShoppingList> {
    private final IngredientDAODB ingredientDAO = new IngredientDAODB();
    private final UnitDAODB unitDAO = new UnitDAODB();

    public ShoppingListDAODB() throws AccessException {
    }

    @Override
    protected String getStringDelete() {
        return "DELETE FROM SHOPPING_LIST where shopping_list_name=?";
    }

    @Override
    protected String getStringSelect() {
        return "select * " +
                "from (((shopping_list_ingredient natural join shopping_list) natural join (ingredient natural join ingredient_type))" +
                "natural join unit) where ";
    }

    @Override
    protected String getStringSelectGroupBy() {
        return "GROUP BY shopping_list_name";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT * FROM shopping_list " +
                "natural join shopping_list_ingredient group by shopping_list_name";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT shopping_list_id FROM SHOPPING_LIST where shopping_list_name =?";
    }

    @Override
    protected String getIDColumnName() {
        return "shopping_list_id";
    }


    private Vector<IngredientRecipe> getIngredients(String name) {
        Vector<IngredientRecipe> ingredients = new Vector<>();
        String request = "select quantity,unit_name,ingredient_name,ingredient_type_name " +
                "from (((shopping_list_ingredient natural join shopping_list) natural join (ingredient natural join ingredient_type))natural join unit) " +
                "where shopping_list_name=? order by ingredient_type_id ";
        try (PreparedStatement stmt = conn.prepareStatement(request)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String chosenUnit = rs.getString("unit_name");
                int quantity = rs.getInt("quantity");
                ingredients.add(new IngredientRecipe(ingredientDAO.buildModelObj(rs), new Unit(chosenUnit), quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    @Override
    ShoppingList buildModelObj(final ResultSet rs) throws SQLException {
        try {
            String name = rs.getString("shopping_list_name");
            Vector<IngredientRecipe> ingredients = getIngredients(name);
            boolean archived = rs.getBoolean("archive");
            ShoppingList shoppingList = new ShoppingList(name, ingredients);
            if (archived) {shoppingList.archive();}
            return shoppingList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Filter<ShoppingList> makeBasicFilter(ShoppingList obj) {
        Filter<ShoppingList> filter = new Filter<>();
        try {
            filter.addField(new ShoppingListFieldName(obj.getName()));
        } catch (FilterException e) {
            e.printStackTrace();
        }
        return filter;
    }


    @Override
    protected void insert(ShoppingList list, Integer id) throws AccessException {
        if (isPresent(list)) throw new DoubleInsertException();
        String insertShoppingList = "INSERT into SHOPPING_LIST(shopping_list_id,shopping_list_name,archive) values(?,?,?)";
        try (PreparedStatement stmt = getPreparedStatement(insertShoppingList)) {
            if (id == null) stmt.setNull(1, java.sql.Types.INTEGER);
            else stmt.setInt(1, id);
            stmt.setString(2, list.getName());
            stmt.setBoolean(3, list.isArchived());
            stmt.executeUpdate();
            insertShoppingListIngredient(list);
        } catch (SQLException e) {
            throw new AccessException(e);
        }
    }

    private void insertShoppingListIngredient(ShoppingList list) throws AccessException {
        String insertShoppingListIngredient = "INSERT into SHOPPING_LIST_INGREDIENT(shopping_list_id,ingredient_id,unit_id,quantity) values(?,?,?,?)";
        int shoppingListID = getID(list);
        int ingredientId;
        int unitId;
        try (PreparedStatement stmt2 = getPreparedStatement(insertShoppingListIngredient)) {
            for (IngredientRecipe ingredientRecipe : list.getIngredients()) {
                ingredientId = ingredientDAO.getID(ingredientRecipe.getIngredient().getIngredient());
                unitId = unitDAO.getID(ingredientRecipe.getUnit());
                stmt2.setInt(1, shoppingListID);
                stmt2.setInt(2, ingredientId);
                stmt2.setInt(3, unitId);
                stmt2.setInt(4, ingredientRecipe.getQuantity());
                stmt2.addBatch();
            }
            stmt2.executeBatch();
        } catch (SQLException e) {
            throw new AccessException(e);
        }

    }


    @Override
    public void update(ShoppingList list) throws AccessException {
        // remove old values
        int id = getID(list);
        String deleteOldShopIngr = "DELETE from SHOPPING_LIST_INGREDIENT WHERE shopping_list_id=?";
        try (PreparedStatement stmt = getPreparedStatement(deleteOldShopIngr)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException | ConnectionException e) {
            e.printStackTrace();
        }

        // set new values
        insertShoppingListIngredient(list);
        String updateArchive = "UPDATE SHOPPING_LIST set archive=? where shopping_list_id=?";
        try (PreparedStatement stmt = getPreparedStatement(updateArchive)) {
            stmt.setBoolean(1, list.isArchived());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AccessException(e);
        }
    }
}