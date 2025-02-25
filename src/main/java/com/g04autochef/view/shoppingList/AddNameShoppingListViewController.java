package com.g04autochef.view.shoppingList;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller used when using the Add Shopping list button.
 */
public class AddNameShoppingListViewController extends ViewController {

    private AddNameShoppingListListener displayShoppingListController;

    @FXML private Button confirmButton;
    @FXML private TextField nameShoppingTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setListener(AddNameShoppingListListener displayShoppingListController) {
        this.displayShoppingListController = displayShoppingListController;
    }

    @FXML
    void confirmNameShoppingList() throws AccessException, FilterException {
        displayShoppingListController.confirmAndReturn(nameShoppingTextField.getText());
    }

    @FXML
    void returnToShoppingList() {
        displayShoppingListController.cancelAndReturn();
    }

    @FXML
    void updateConfirmButton() {
        confirmButton.setDisable(nameShoppingTextField.getText().isBlank());
    }

    /**
     * Listener to allow communication.
     */
    public interface AddNameShoppingListListener {
        void cancelAndReturn();
        void confirmAndReturn(String name) throws AccessException, FilterException;
    }
}
