package com.g04autochef.view.shoppingList;

import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.IngredientTableViewConstructor;
import com.g04autochef.view.tableConstructors.ShoppingListTableViewContructor;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Controller View for displaying the archived shopping lists
 */
public class ArchivedShoppingListViewController extends ViewController implements TableViewable<TableViewable.TableViewListener> {

    private ArchivedShoppingListListener shoppingListController;
    private ShoppingList currentlySelectedShoppingList;

    @FXML private Button unarchiveButton;
    @FXML private Button deleteButton;

    @FXML private TableView<ShoppingList> shoppingListTableView;
    @FXML private TableColumn<ShoppingList, String> titleTableColumn;

    @FXML private TableView<IngredientRecipe> productsTableView;
    @FXML private TableColumn<IngredientRecipe,String> nameTableColumn;
    @FXML private TableColumn<IngredientRecipe,String> categoryTableColumn;
    @FXML private TableColumn<IngredientRecipe,String> quantityTableColumn;
    @FXML private TableColumn<IngredientRecipe,String> unitTableColumn;

    public void setListener(final ArchivedShoppingListListener archivedShoppingListViewController) {
        this.shoppingListController = archivedShoppingListViewController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unarchiveButton.setDisable(true);
        deleteButton.setDisable(true);
        shoppingListTableView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedShoppingList = newValue;
                displayShoppingList(newValue);
                unarchiveButton.setDisable(false);
                deleteButton.setDisable(false);
            }else{
                unarchiveButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    @FXML
    private void returnToShoppingListView() {
        shoppingListController.returnToShoppingList();
    }

    /**
     * Display the ingredients of a shopping list
     * @param list the current shopping list whose ingredients we want to display
     */
    public void displayShoppingList(final ShoppingList list) {
        Vector<IngredientRecipe> listProducts= list.getIngredients();
        ObservableList<IngredientRecipe> products = FXCollections.observableArrayList();
        products.addAll(listProducts);
        productsTableView.setItems(products);
    }

    /**
     * Initialize the table view by adding all of the shopping lists
     * @param shoppingList the vector of shopping lists
     */
    public void setShoppingList(Vector<ShoppingList> shoppingList){
        shoppingListTableView.setItems(FXCollections.observableArrayList(shoppingList));
        shoppingListTableView.getSelectionModel().selectFirst();
    }

    /**
     * Restores list from the archived section.
     */
    @FXML
    private void unarchiveList() {
        currentlySelectedShoppingList.unArchive();
        shoppingListController.updateShoppingListController(currentlySelectedShoppingList);
        removeCurrentShoppingListTableView();
        AlertBoxGenerator.showInfo("La Liste de courses a bien été restaurée", "");
    }

    @FXML
    private void deleteList() {
        removeCurrentShoppingListTableView();
        shoppingListController.deleteShoppingList(currentlySelectedShoppingList);
    }

    /**
     * Remove the currently selected shipping list.
     */
    private void removeCurrentShoppingListTableView() {
        if (currentlySelectedShoppingList != null)
            shoppingListTableView.getItems().remove(currentlySelectedShoppingList);
        if (shoppingListTableView.getItems().size() == 0)
            productsTableView.getItems().clear();
    }

    public void definitionTableViewProduct() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(List.of(nameTableColumn, categoryTableColumn, quantityTableColumn,
                unitTableColumn));
        defineTableView(new IngredientTableViewConstructor(), productsTableView, listColumns);
    }

    public void definitionTableViewShoppingList(){
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(List.of(titleTableColumn));
        defineTableView(new ShoppingListTableViewContructor(), shoppingListTableView, listColumns);
    }

    public interface ArchivedShoppingListListener extends TableViewListener {
        void returnToShoppingList();
        void deleteShoppingList(ShoppingList shoppingList);
        void updateShoppingListController(ShoppingList shoppingList);
    }
}