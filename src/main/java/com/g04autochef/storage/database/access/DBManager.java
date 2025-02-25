package com.g04autochef.storage.database.access;

import java.sql.*;

import org.sqlite.SQLiteConfig;


/**
 * Singleton representing the database
 */
public class DBManager {

    private static DBManager instance;
    private final String DB_NAME ;
    private Connection connection = null;

    private DBManager() {
        this("BDD");
    }

    private DBManager(String dbName){
        DB_NAME = dbName;
        dbConnect();
    }

    /**
     * {@return returns the singleton instance}
     */
    public static DBManager getInstance() {
        if (instance == null) instance = new DBManager();
        return instance;
    }

    public static DBManager getInstance(String dbName){
        if (instance == null) instance = new DBManager(dbName);
        return instance;
    }

    /**
     * Get the current DB connection, create it if the current connection is null
     *
     * @return the DB connection
     * @throws SQLException exception thrown if the connection failed
     */
    public Connection getDBConnection() throws SQLException {
        return connection;
    }

    public void dbConnect() {
        try {
            final String DRIVER = "org.sqlite.JDBC";
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            final String DB_URL = "jdbc:sqlite:./" + DB_NAME;
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void dbDisconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Create all the tables
     */
    public void dbCreateAllTables() {
        // createPictureTable();
        createUnitTable();

        createIngredientTypeTable();
        createIngredientTable();

        createRecipeCookingStyleTable();
        createRecipeTypeTable();
        createRecipeTable();
        createRecipePictureTable();

        createShoppingListTable();

        createDayTimeTable();
        createDayOfTheWeekTable();
        createWeekListTable();
        createMenuTable();

        createMenuRecipeTable();
        createMenuDayOfTheWeek();

        createRecipeIngredientTable();
        createRecipeInstructionTable();
        createIngredientUnitTable();
        createShoppingListIngredientTable();


        createShopsTable();
        createShopsPriceTable();
    }

    /**
     * Base methode to create a table
     */
    private void _createTable(String instructions) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            String sql = "CREATE table IF NOT EXISTS " + instructions;

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) { ignored.printStackTrace(); }
        }
    }

    private void createUnitTable() {
        _createTable("unit(" +
                "unit_id integer primary key," +
                "unit_name varchar(20) unique not null" +
                ")");
    }

    private void createIngredientUnitTable() {
        _createTable("ingredient_unit(" +
                "ingredient_id integer, " +
                "unit_id integer," +
                "primary key (ingredient_id, unit_id), " +
                "foreign key (ingredient_id) references ingredient (ingredient_id) on delete cascade," +
                "foreign key (unit_id) references unit (unit_id) on delete cascade" +
                ")");
    }

    private void createIngredientTable() {
        _createTable("ingredient(" +
                "ingredient_id integer primary key, " +
                "ingredient_name varchar(100) unique not null," +
                "ingredient_type_id integer not null, " +
                "foreign key (ingredient_type_id) references ingredient_type (ingredient_type_id) on delete cascade" +
                ")");
    }

    private void createIngredientTypeTable() {
        _createTable("ingredient_type(" +
                "ingredient_type_id integer primary key, " +
                "ingredient_type_name varchar(20) unique not null" +
                ")");
    }

    private void createRecipeTable() {
        _createTable("recipe(" +
                "recipe_id integer primary key, " +
                "recipe_name varchar(50) unique not null, " +
                "recipe_cooking_style_id integer, " +
                "number_people integer, " +
                "recipe_type_id integer, " +
                "foreign key (recipe_type_id) references recipe_type (recipe_type_id) on delete cascade, " +
                "foreign key (recipe_cooking_style_id) references recipe_cooking_style (recipe_cooking_style_id) on delete cascade " +
                ")");
    }
    private void createRecipePictureTable() {
        _createTable("recipe_picture(" +
                "recipe_id integer primary key, " +
                "picture_path varchar(50) not null, " +
                "foreign key (recipe_id) references recipe (recipe_id) on delete cascade " +
                ")");
    }


    private void createRecipeTypeTable() {
        _createTable("recipe_type(" +
                "recipe_type_id integer primary key, " +
                "recipe_type_name varchar(20) unique not null" +
                ")");
    }

    private void createRecipeInstructionTable() {
        _createTable("recipe_instruction(" +
                "recipe_id integer, " +
                "step_number integer, " +
                "instruction varchar(500)," +
                "primary key (recipe_id, step_number), " +
                "foreign key (recipe_id) references recipe (recipe_id) on delete cascade" +
                ")");
    }

    private void createRecipeIngredientTable() {
        _createTable("recipe_ingredient(" +
                "recipe_id integer, " +
                "ingredient_id integer, " +
                "quantity integer, " +
                "unit_id integer, " +
                "primary key (recipe_id, ingredient_id), " +
                "foreign key (recipe_id) references recipe (recipe_id) on delete cascade, " +
                "foreign key (unit_id) references unit (unit_id) on delete cascade, " + // shoud be a hook testing if this unit is allowed !!
                "foreign key (ingredient_id) references ingredient (ingredient_id) on delete cascade" +
                ")");
    }

    private void createShoppingListTable() {
        _createTable("shopping_list(" +
                "shopping_list_id integer primary key, " +
                "shopping_list_name varchar(20) unique not null, " +
                "archive boolean" +
                ")");
    }

    private void createShoppingListIngredientTable() {
        _createTable("shopping_list_ingredient(" +
                "shopping_list_id integer, " +
                "ingredient_id integer, " +
                "unit_id integer, quantity integer, " +
                "primary key (shopping_list_id, ingredient_id), " +
                "foreign key (shopping_list_id) references shopping_list (shopping_list_id) on delete cascade, " +
                "foreign key (ingredient_id) references ingredient (ingredient_id) on delete cascade, " +
                "foreign key (unit_id) references unit (unit_id) on delete cascade" +
                ")"); // shoud be a hook testing if this unit is allowed !!
    }


    private void createMenuTable() {
        _createTable("menu(" +
                "menu_id integer primary key, " +
                "menu_name varchar(30) unique not null" +
                ")");
    }

    private void createWeekListTable() {
        _createTable("week_list(" +
                "week_list_id integer primary key," +
                "week_list_name varchar(30) unique not null" +
                ")");
    }

    private void createMenuDayOfTheWeek() {
        _createTable("menu_day_of_the_week(" +
                "week_list_id integer, " +
                "menu_id integer, " +
                "day_id integer, " +
                "primary key (week_list_id,menu_id, day_id), " +
                "foreign key (week_list_id) references week_list (week_list_id) on delete cascade, " +
                "foreign key (menu_id) references menu(menu_id) on delete cascade, " +
                "foreign key (day_id) references day_of_the_week(day_id) on delete cascade" +
                ")");
    }


    private void createMenuRecipeTable() {
        _createTable("menu_recipe(" +
                "menu_id integer, " +
                "recipe_id integer, " +
                "day_time_id integer, " +
                "menu_number_people integer, " +
                "primary key (menu_id, recipe_id, day_time_id), " +
                "foreign key (menu_id) references menu (menu_id) on delete cascade, " +
                "foreign key (recipe_id) references recipe (recipe_id) on delete cascade, " +
                "foreign key (day_time_id) references day_time (day_time_id) on delete cascade" +
                ")");
    }

    private void createDayTimeTable() {
        _createTable("day_time(" +
                "day_time_id integer primary key, " +
                "day_time_name varchar(20) unique not null" +
                ")");
    }

    private void createDayOfTheWeekTable() {
        _createTable("day_of_the_week(" +
                "day_id integer primary key, " +
                "day_of_the_week_name varchar(20) unique not null" +
                ")");
    }

    private void createShopsTable() {
        _createTable("shop(" +
                "shop_id integer primary key, " +
                "shop_name varchar(100) not null unique," +
                "shop_address varchar(100)" +
                ")");
    }

    private void createShopsPriceTable() {
        _createTable("shop_ingredient_price(" +
                "shop_id integer not null," +
                "ingredient_id integer not null," +
                "unit_id integer not null," +
                "price double not null," +
                "quantity double not null," +
                "primary key (shop_id, ingredient_id, unit_id)," +
                "foreign key (shop_id) references shop (shop_id) on delete cascade," +
                "foreign key (ingredient_id) references ingredient (ingredient_id) on delete cascade," +
                "foreign key (unit_id) references unit (unit_id) on delete cascade" +
                ")");
    }

    private void createRecipeCookingStyleTable() {
        _createTable("recipe_cooking_style(" +
                "recipe_cooking_style_name varchar(20) unique not null, " +
                "recipe_cooking_style_id integer primary key" +
                ")");
    }

    public String getName(){return DB_NAME;}



    public static void main(String[] args) throws SQLException {
        // DB manager main for populating or testing
        getInstance().dbCreateAllTables();
        getInstance().dbDisconnect();
        }
}

