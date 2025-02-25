package com.g04autochef.view.mainMenu;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuViewController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    private MenuViewListener menuController;

    @FXML protected void displayShoppingList() {
        menuController.displayShoppingList();
    }
    @FXML protected void displayRecipeList() {
        menuController.displayRecipeList();
    }
    @FXML protected void displayMenuManager() {
        menuController.displayMenuManager();
    }

    @FXML protected void diplayAddProduct() {
        menuController.displayAddProduct();
    }

    @FXML protected void displayShopManager(){
        menuController.displayShopManager();
    }

    /**
     * @param listener the menuController for having a reference to it
     */
    public void setListener(MenuViewListener listener){
        this.menuController = listener;
    }

    /**
     * Listener that the menuController will implement it in order to have a reference to it
     */
    public interface MenuViewListener{
        void displayShoppingList();
        void displayRecipeList();
        void displayMenuManager();
        void displayAddProduct();
        void displayShopManager();
    }
}
