package com.g04autochef.controller;

import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.mainMenu.MainMenuViewController;
import com.g04autochef.view.Windows;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController extends Controller implements MainMenuViewController.MenuViewListener {

    private final MenuListener mainController;
    private final Stage stage;

    /**
     * @param stage Window will contain the sscene
     * @param mainController reférence to the MainController
     */
    public MenuController(Stage stage, MenuListener mainController){
        this.mainController = mainController;
        this.stage = stage;
    }

    /**
     * Method overriden from Controller
     * will load the View and show it
     */
    @Override
    public void show() {
        try {
            FXMLLoader loader = getFxmlLoader(stage, MainMenuViewController.class.getResource(Windows.MainMenu.getPathToFXML()));
            MainMenuViewController menuPrincipalViewController = loader.getController();
            menuPrincipalViewController.setListener(this);
        } catch (IOException e) {
            AlertBoxGenerator.showError("Erreur lors du chargement du menu principale", e);
        }
    }

    /**
     * Show the shopping list by calling the MainController
     */
    @Override
    public void displayShoppingList() {
        mainController.displayShoppingList();
    }

    /**
     * Show the recipe list by calling the MainController
     */
    @Override
    public void displayRecipeList() { mainController.displayRecipeList(); }

    /**
     * Show the menu manager by calling the MainController
     */
    @Override
    public void displayMenuManager() {
        mainController.displayMenuManager();
    }

    /**
     * Show the add product page by calling the MainController
     */
    @Override
    public void displayAddProduct() {
        mainController.displayAddProduct();
    }

    @Override
    public void displayShopManager() {
        mainController.displayShopManager();
    }


    /**
     * Interface implemented by the MainController in order to communicate
     * With the MenuController
     */
    public interface MenuListener{
        void displayShoppingList();
        void displayRecipeList();
        void displayMenuManager();
        void displayAddProduct();
        void displayShopManager();
    }
}
