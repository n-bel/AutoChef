package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.Updatable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.DoubleInsertException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.menuWeeklyFields.MenuWeeklyFieldName;
import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.storableDAO.MenuDaily;
import com.g04autochef.model.storableDAO.MenuWeekly;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class MenuWeeklyDAODB extends DAO_DB<MenuWeekly> implements Updatable<MenuWeekly> {
    private final MenuDailyDAODB menuDailyDAO = new MenuDailyDAODB();

    public MenuWeeklyDAODB() throws AccessException{}

    private int getDayID(DayOfTheWeek day) throws SQLException{
        String request = "SELECT day_id FROM DAY_OF_THE_WEEK  where day_of_the_week_name=?";
        int id = 0;
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            stmt.setString(1, day.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) id = rs.getInt("day_id");
        }
        catch (SQLException ignored) {}
        return id;
    }

    private HashMap<DayOfTheWeek, MenuDaily> getMenus(String name) {
        HashMap<DayOfTheWeek, MenuDaily> menus = new HashMap<>();
        String request ="select *  from (WEEK_LIST natural join( menu_day_of_the_week natural join menu) " +
                "natural join DAY_OF_THE_WEEK) where week_list_name=?";
        try (PreparedStatement stmt = conn.prepareStatement(request)) {
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String day = rs.getString("day_of_the_week_name");
                menus.put(DayOfTheWeek.valueOf(day),menuDailyDAO.buildModelObj(rs));
            }
        }
        catch (SQLException ignored){}
        return menus;
    }

    @Override
    protected String getStringDelete() {
        return "DELETE FROM week_list where week_list_name=?";
    }

    @Override
    protected String getStringSelect() {
        return "select week_list_name from WEEK_LIST where ";
    }

    @Override
    protected String getStringSelectAll() {
        return "SELECT week_list_name FROM WEEK_LIST ";
    }

    @Override
    protected String getStringGetID() {
        return "SELECT week_list_id FROM week_list  where week_list_name=?";
    }

    @Override
    protected String getIDColumnName() {
        return "week_list_id";
    }

    @Override
    MenuWeekly buildModelObj(final ResultSet rs) throws SQLException{
        String name = rs.getString("week_list_name");
        HashMap<DayOfTheWeek, MenuDaily> weekList= getMenus(name);
        return new MenuWeekly(name,weekList);
    }

    @Override
    protected Filter<MenuWeekly> makeBasicFilter(MenuWeekly obj){
        Filter<MenuWeekly> filter=new Filter<>();
        try{filter.addField(new MenuWeeklyFieldName(obj.getName()));}
        catch(FilterException e){e.printStackTrace();}
        return filter;}


    @Override
    protected void insert(MenuWeekly weekList,Integer id) throws AccessException{
        if (isPresent(weekList)) throw new DoubleInsertException();
        String insertMenu = "INSERT into WEEK_LIST (week_list_id,week_list_name) values(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertMenu)){
            if (id==null) stmt.setNull(1,java.sql.Types.INTEGER);
            else stmt.setInt(1,id);
            stmt.setString(2,weekList.getName());
            stmt.executeUpdate();
            insertMenuDayOfTheWeek(weekList);
        }
        catch (SQLException e){throw new AccessException(e);}
    }


    private void insertMenuDayOfTheWeek(MenuWeekly menuWeekly) throws AccessException {
        String insertMenuWeekList  = "INSERT into MENU_DAY_OF_THE_WEEK(week_list_id,menu_id,day_id) values(?,?,?)";
        int weekListID = getID(menuWeekly);
        int menuID;
        int dayTimeID;
        try (PreparedStatement stmt2 = conn.prepareStatement(insertMenuWeekList)) {
            for(Map.Entry<DayOfTheWeek , MenuDaily> entry: menuWeekly.getAll().entrySet()){
                MenuDaily value= entry.getValue();
                menuID = menuDailyDAO.getID(value);
                dayTimeID=getDayID(entry.getKey());
                stmt2.setInt(1,weekListID);
                stmt2.setInt(2,menuID);
                stmt2.setInt(3,dayTimeID);
                stmt2.addBatch();
            }
            stmt2.executeBatch();
        } catch (SQLException e) {
            throw new AccessException(e);
        }
    }

    @Override
    public void update(MenuWeekly menuWeekly) throws AccessException {
        // remove old values
        String deleteOldDaily = "DELETE from MENU WHERE menu_name=?";
        for (int i = 0; i < 7; ++i) {
            String menuDailyName = menuWeekly.getName() + i;
            try(PreparedStatement stmt = getPreparedStatement(deleteOldDaily)) {
                stmt.setString(1, menuDailyName);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new AccessException(e);
            }
        }
        // set new values
        for(Map.Entry<DayOfTheWeek , MenuDaily> entry: menuWeekly.getAll().entrySet()){
            menuDailyDAO.insert(entry.getValue());
        }
        insertMenuDayOfTheWeek(menuWeekly);
    }

}
