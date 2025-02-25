package com.g04autochef.view.shoppingList;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.MenuToShoppingListConverter;
import com.g04autochef.model.storableDAO.MenuWeekly;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class GenerateShoppingListFromMenuViewController extends ViewController {

    @FXML private ComboBox<String> menuComboBox;
    private GenerateShoppingListFromMenuListenerSLC shoppingListController;
    private ShoppingList currentShoppingList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initializeMenuComboBox() throws AccessException {
        Vector<MenuWeekly> menuWeeklyVector =  shoppingListController.requestMenus();
        for (MenuWeekly menuWeekly: menuWeeklyVector){
            menuComboBox.getItems().add(menuWeekly.getName());
        }
        menuComboBox.getSelectionModel().selectFirst();
    }

    public void setListener(GenerateShoppingListFromMenuListenerSLC shoppingListController) {
        this.shoppingListController = shoppingListController;
    }

    public void setCurrentShoppingList(ShoppingList shoppingList) {
        this.currentShoppingList = shoppingList;
    }

    /**
     * Creates the shopping list from the Menu
     */
    @FXML
    private void confirmSelectedMenu() throws AccessException, FilterException {
        MenuWeekly selectedMenu = shoppingListController.requestMenu(menuComboBox.getValue()).get(0);
        MenuToShoppingListConverter.AddMenuToShoppingList(currentShoppingList, selectedMenu);
        shoppingListController.confirmSelectedMenu(currentShoppingList);
    }

    @FXML
    private void cancelAndReturn() {
        shoppingListController.cancelAndReturn();
    }

    public interface GenerateShoppingListFromMenuListenerSLC {
        void confirmSelectedMenu(ShoppingList shoppingList);
        void cancelAndReturn();
        Vector<MenuWeekly> requestMenus() throws AccessException;
        Vector<MenuWeekly> requestMenu(String menuName) throws AccessException, FilterException;
    }
}
