package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.menuDailyFields.MenuDailyFieldName;
import com.g04autochef.model.storableDAO.MenuDaily;
import com.g04autochef.model.storableDAO.Recipe;
import com.g04autochef.model.TimeOfTheDay;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class MenuDailyDAODB extends DAO_DB<MenuDaily> {
    private final RecipeDAODB recipeDAO = new RecipeDAODB();

    public MenuDailyDAODB() throws AccessException {}

    private int getDayTimeID(TimeOfTheDay dayTime) {
        String request = "SELECT day_time_id FROM DAY_TIME  where day_time_name=?";
        int id = 0;
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1, dayTime.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) id = rs.getInt("day_time_id");
        }
        catch (SQLException ignored) {}
        return id;
    }

    private HashMap<TimeOfTheDay, Recipe> getRecipes(String menu_name) {
        HashMap<TimeOfTheDay, Recipe> menu  = new HashMap<>();
        String request = "SELECT recipe_name,number_people,recipe_type_name,day_time_name,recipe_cooking_style_name " +
                "FROM (MENU natural join ( MENU_recipe natural join recipe natural join recipe_type natural join recipe_cooking_style) " +
                "natural join day_time) where menu_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,menu_name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String timeOfTheDay = rs.getString("day_time_name");
                menu.put(TimeOfTheDay.valueOf(timeOfTheDay),recipeDAO.buildModelObj(rs));
            }
        }
        catch (SQLException e){e.printStackTrace();}
        return menu;
    }

    private HashMap<TimeOfTheDay, Integer> getDayTimePeople(String menu_name) {
        HashMap<TimeOfTheDay, Integer> people= new HashMap<>();
        String request = "SELECT menu_number_people,day_time_name " +
                "FROM (MENU natural join ( MENU_recipe natural join recipe natural join recipe_type)" +
                "natural join day_time) where menu_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1,menu_name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String timeOfTheDay = rs.getString("day_time_name");
                people.put(TimeOfTheDay.valueOf(timeOfTheDay),rs.getInt("menu_number_people"));
            }
        }
        catch (SQLException ignored){}
        return people;
    }


    @Override
    protected String getStringDelete() {
        return "DELETE FROM MENU where menu_name=?";
    }

    @Override
    protected String getStringSelect() {
        return "SELECT menu_name,recipe_name,number_people,recipe_type_name,day_time_name " +
                "FROM (MENU natural join MENU_recipe natural join recipe natural join " +
                "recipe_type natural join DAY_TIME) WHERE ";
    }

    @Override
    protected String getStringSelectGroupBy() {
        return "GROUP BY menu_name,day_time_name,recipe_name";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT menu_name FROM MENU ";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT menu_id FROM MENU where menu_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "menu_id";
    }

    @Override
    MenuDaily buildModelObj(final ResultSet rs) throws SQLException {
       String name = rs.getString("menu_name");
       HashMap<TimeOfTheDay, Recipe> menuRecipes = getRecipes(name);
       HashMap<TimeOfTheDay, Integer> dayTimePeople = getDayTimePeople(name);
       return new MenuDaily(name,menuRecipes,dayTimePeople);
    }

    @Override
    protected Filter<MenuDaily> makeBasicFilter(MenuDaily obj){
        Filter<MenuDaily> filter=new Filter<>();
        try{filter.addField(new MenuDailyFieldName(obj.getName()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}

    @Override
    protected void insert(MenuDaily menu,Integer id) throws AccessException {
        if (isPresent(menu)) throw new DoubleInsertException();
        String insertMenu = "INSERT into MENU (menu_id,menu_name) values(?,?)";
        String insertMenuRecipe = "INSERT into MENU_RECIPE(menu_id,recipe_id,day_time_id,menu_number_people) " +
                "values(?,?,?,?)";

        try (PreparedStatement stmt = getPreparedStatement(insertMenu);
             PreparedStatement stmt2= getPreparedStatement(insertMenuRecipe)){
            if(id==null)stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,menu.getName());
            stmt.executeUpdate();
            int menuID = getID(menu);
            for(Map.Entry<TimeOfTheDay , Recipe> entry: menu.getRecipes().entrySet()){
                Recipe value= entry.getValue();
                stmt2.setInt(1,menuID);
                stmt2.setInt(2,recipeDAO.getID(value));
                stmt2.setInt(3,getDayTimeID(entry.getKey()));
                stmt2.setInt(4,menu.getDaytimePeople(entry.getKey()));
                stmt2.addBatch();
            }
            stmt2.executeBatch();

        } catch (SQLException e) {throw new AccessException(e);}
    }
}