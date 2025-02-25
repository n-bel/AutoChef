package com.g04autochef.controller;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.storage.database.access.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Controller coordinates stage and scene changes.
 * Communicates with model to answer queries made by sub-interfaces.
 * Must implement all interfaces with it acts as a Listener for.
 */
public class MainController extends Application implements Controller.ControllerListener, MenuController.MenuListener {

    private MenuController menuController;
    private ShoppingListController<ShoppingListDAODB,IngredientDAODB,MenuWeeklyDAODB> shoppingListController;
    private RecipeController<RecipeDAODB, RecipeCookingStyleDAODB, IngredientDAODB> recipeController;
    private MenusController<MenuWeeklyDAODB, RecipeDAODB, RecipeTypeDAODB, MenuDailyDAODB, ShoppingListDAODB, RecipeCookingStyleDAODB> menusController;
    private AddIngredientController<UnitDAODB, IngredientTypeDAODB, IngredientDAODB> addIngredientController;
    private MapController<ShopDAODB, ShopIngredientPriceDAODB,  IngredientDAODB> mapController;

    /**
     * @param primaryStage Window that will contain the View
     * Will load the main menu of the App
     * Overriden from the MenuController.MenuListener
     */
    @Override
    public void start(Stage primaryStage) throws AccessException {
        ShoppingListDAODB shoppingListDAO = new ShoppingListDAODB();
        IngredientDAODB ingredientDAO = new IngredientDAODB();
        MenuWeeklyDAODB menuWeeklyDAO = new MenuWeeklyDAODB();
        ShopDAODB shopDAO = new ShopDAODB();
        UnitDAODB unitDAO = new UnitDAODB();
        RecipeDAODB recipeDAO = new RecipeDAODB();
        RecipeCookingStyleDAODB recipeCookingStyleDAO = new RecipeCookingStyleDAODB();
        RecipeTypeDAODB recipeTypeDAO = new RecipeTypeDAODB();
        MenuDailyDAODB menuDailyDAO = new MenuDailyDAODB();
        IngredientTypeDAODB ingredientTypeDAO = new IngredientTypeDAODB();
        ShopIngredientPriceDAODB shopIngredientPriceDAO = new ShopIngredientPriceDAODB();

        menuController = new MenuController(primaryStage, this);
        shoppingListController = new ShoppingListController<>(primaryStage, this, shoppingListDAO, ingredientDAO, menuWeeklyDAO);
        recipeController = new RecipeController<>(primaryStage, this, recipeDAO, recipeCookingStyleDAO, ingredientDAO);
        menusController = new MenusController<>(primaryStage, this, menuWeeklyDAO, recipeDAO, recipeTypeDAO, menuDailyDAO, shoppingListDAO, recipeCookingStyleDAO);
        addIngredientController = new AddIngredientController<>(primaryStage, this, unitDAO, ingredientTypeDAO, ingredientDAO);
        mapController = new MapController<>(primaryStage, this,shopDAO, ingredientDAO,shopIngredientPriceDAO);
        loadMainMenu();
    }

    /**
     * Window that will contain the View
     */
    private void loadMainMenu() {
        menuController.show();
    }

    public static void launchApp(){
        Application.launch();
    }

    /**
     * Overriden from ShoppingListController.ShoppingListListener
     */
    @Override
    public void displayShoppingList() {
        shoppingListController.show();
    }

    /**
     * Overriden from RecipeController.RecipeListener
     */
    @Override
    public void displayRecipeList() {
        recipeController.show();
    }

    /**
     * Overriden from MenuController.MenuListener
     */
    @Override
    public void displayMenuManager() {
        menusController.show();
    }

    /**
     * Overriden from MenuController.MenuListener
     */
    @Override
    public void displayAddProduct() {
        addIngredientController.show();
    }

    public void displayShopManager(){
        mapController.show();
    }

    /**
     * Overriden from all sub Controllers
     */
    @Override
    public void returnToMainMenu() {
        loadMainMenu();
    }
}
