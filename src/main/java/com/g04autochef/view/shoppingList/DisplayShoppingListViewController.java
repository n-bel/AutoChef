package com.g04autochef.view.shoppingList;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.ShoppingList;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.FXComponents.IngredientWithQuantityFX;
import com.g04autochef.view.IntegerSpinner;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.IngredientTableViewConstructor;
import com.g04autochef.view.tableConstructors.ShoppingListTableViewContructor;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Controller View for displaying and editing the shopping list
 */
public class DisplayShoppingListViewController extends ViewController
        implements TableViewable<TableViewable.TableViewListener>, IngredientWithQuantityFX.IngredientWithQuantityFXListener,
            AddNameShoppingListViewController.AddNameShoppingListListener {

    private DisplayShoppingListListener shoppingListController;
    private ShoppingList currentlySelectedShoppingList;

    @FXML private TableView<ShoppingList> shoppingListTableView;
    @FXML private TableColumn<ShoppingList, String> titleTableColumn;
    @FXML private Button addIngredientButton;
    @FXML private Button deleteButton;
    @FXML private Button archiveButton;
    @FXML private Button exportPDFButton;
    @FXML private Button generateShoppingListButton;
    @FXML private Button exportQrButton;

    @FXML private TableView<IngredientWithQuantityFX> productsTableView;
    @FXML private TableColumn<IngredientWithQuantityFX, TextField> nameTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, String> categoryTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, IntegerSpinner> quantityTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, ComboBox<String>> unitTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, Button> deleteTableColumn;

    /**
     * @param shoppingList reference to the shopping list controller
     */
    public void setListener(DisplayShoppingListListener shoppingList){
        this.shoppingListController = shoppingList;
    }

    private void disableButtons(boolean bool) {
        deleteButton.setDisable(bool);
        archiveButton.setDisable(bool);
        exportPDFButton.setDisable(bool);
        addIngredientButton.setDisable(bool);
        generateShoppingListButton.setDisable(bool);
        exportQrButton.setDisable(bool);
    }

    /**
     * Initialize the buttons and the listener for knowing which product we are selecting
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableButtons(true);
        shoppingListTableView.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> {
                if (newValue != null) {
                        disableButtons(false);
                        try {
                            if (shoppingListTableView.getItems().contains(oldValue))
                                updateShoppingList(oldValue);
                            cleanCurrentShoppingLists();
                            displayShoppingList(newValue);
                        } catch (FilterException | AccessException e) {
                            AlertBoxGenerator.showError("Erreur lors de l'initialisation de la vue des listes de courses", e);
                        }
                        currentlySelectedShoppingList = newValue;
                    }
                    else{
                        disableButtons(true);
                    }
                });
    }

    public ShoppingList getCurrentlySelectedShoppingList() {
        return currentlySelectedShoppingList;
    }

    /**
     * Removes an invalid or empty ingredient
     */
    private void cleanIngredients() {
        productsTableView.getItems().removeIf(ingredientRecipeFX -> !ingredientRecipeFX.nameIsValid());
    }

    /**
     * Removes a shopping list if it doesn't contain at least one ingredient
     */
    private void cleanCurrentShoppingLists() {
        if (currentlySelectedShoppingList != null && currentlySelectedShoppingList.getIngredients().isEmpty())
            shoppingListTableView.getItems().remove(currentlySelectedShoppingList);
    }

    /**
     * Save the modifications of the ingredients of the shopping list
     * @param shoppingList the selected shopping list that we want to update
     */
    private void updateShoppingList(ShoppingList shoppingList) {
        if (shoppingList != null && shoppingListTableView.getItems().contains(shoppingList)){
            saveIngredientsShoppingList(shoppingList);
            shoppingListController.updateShoppingListController(shoppingList);
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void returnToMainMenu() throws IOException {
        updateShoppingList(currentlySelectedShoppingList);
        shoppingListController.returnToMainMenu();
    }

    /**
     * Saves the ingredients into the table view (if all of these ingredients are valid)
     * @param shoppingList the selected shopping list
     */
    private void saveIngredientsShoppingList(ShoppingList shoppingList) {
        cleanIngredients();
        final Vector<IngredientRecipe> ingredientRecipes = makeIngredientRecipesFromFXElements();
        shoppingList.setIngredients(ingredientRecipes);
    }

    private Vector<IngredientRecipe> makeIngredientRecipesFromFXElements() {
        Vector<IngredientRecipe> ingredientRecipes = new Vector<>();
        ObservableList<IngredientWithQuantityFX> listIngredientWithQuantityFXES = productsTableView.getItems();
        for (IngredientWithQuantityFX ingredientWithQuantityFX : listIngredientWithQuantityFXES){
            ingredientRecipes.add(new IngredientRecipe(ingredientWithQuantityFX.FXToIngredientQuantity()));
        }
        return ingredientRecipes;
    }

    /**
     * Action of add shopping list button
     */
    @FXML
    private void createNewShoppingList() throws IOException, AccessException {
        updateShoppingList(currentlySelectedShoppingList);
        shoppingListController.showAddShoppingListView(this);
        ShoppingList oldShoppingList = currentlySelectedShoppingList;
        shoppingListTableView.getSelectionModel().selectLast();
        if (!(oldShoppingList == currentlySelectedShoppingList)){
            createNewIngredient();
        }
    }

    @FXML
    private void deleteCurrentShoppingList() {
        deleteShoppingList(currentlySelectedShoppingList);
    }

    private void deleteShoppingList(final ShoppingList shoppingList) {
        if (shoppingList != null) {
            shoppingListController.deleteShoppingList(shoppingList);
            shoppingListTableView.getItems().remove(shoppingList);
            clearProductsIfShoppingListEmpty();
            AlertBoxGenerator.showInfo("La Liste de courses a bien été supprimée", "");
        }
    }

    private void clearProductsIfShoppingListEmpty() {
        if (shoppingListTableView.getItems().isEmpty())
            productsTableView.getItems().clear();
    }

    @FXML
    private void createNewIngredient() throws AccessException {
        final Vector<String> possibleSuggestionsName = shoppingListController.getPossibleSuggestionsName();
        final IngredientWithQuantityFX ingredientFX = new IngredientWithQuantityFX(possibleSuggestionsName, this);
        productsTableView.getItems().add(ingredientFX);
        productsTableView.getSelectionModel().selectLast();
    }

    @FXML
    private void archiveList() {
        currentlySelectedShoppingList.archive();
        shoppingListController.updateShoppingListController(currentlySelectedShoppingList);
        shoppingListTableView.getItems().remove(currentlySelectedShoppingList);
        if (shoppingListTableView.getItems().isEmpty()) productsTableView.getItems().clear();
        AlertBoxGenerator.showInfo("La Liste de courses a bien été archivée", "");
    }

    /**
     * Call the controller for changing the view
     */
    @FXML
    private void showArchivedList() throws IOException, AccessException, FilterException {
        shoppingListController.showArchivedList();
    }

    /**
     * Load the selected shopping list that we want to display its ingredients
     * @param list the shopping list that we want to display
     */
    public void displayShoppingList(final ShoppingList list) throws AccessException, FilterException {
        productsTableView.getItems().clear();
        final Vector<IngredientRecipe> listProducts= list.getIngredients();
        final ObservableList<IngredientWithQuantityFX> products = FXCollections.observableArrayList();
        final Vector<String> ingredientNames = shoppingListController.getPossibleSuggestionsName();
        for (final IngredientRecipe ingredient : listProducts) {
            Vector<String> unitName = shoppingListController.getUnitNames(ingredient);
            IngredientWithQuantityFX ingredientWithQuantityFX = new IngredientWithQuantityFX(ingredient.getName(),
                    ingredientNames, ingredient.getType(), ingredient.getQuantity(), ingredient.getUnitName(),
                    FXCollections.observableList(unitName), this);
            products.add(ingredientWithQuantityFX);
        }
        productsTableView.setItems(products);
        productsTableView.getSelectionModel().selectFirst();
    }

    /**
     * For displaying the shopping lists onto the tables views
     * @param shoppingList vector of the shopping lists
     */
    public void setShoppingList(final Vector<ShoppingList> shoppingList){
        shoppingListTableView.setItems(FXCollections.observableArrayList(shoppingList));
        shoppingListTableView.getSelectionModel().selectFirst();
    }

    public void definitionTableViewProduct() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(Arrays.asList(nameTableColumn, categoryTableColumn, quantityTableColumn, unitTableColumn, deleteTableColumn));
        defineTableView(new IngredientTableViewConstructor(), productsTableView, listColumns);
    }

    public void definitionTableViewShoppingList(){
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(List.of(titleTableColumn));
        defineTableView(new ShoppingListTableViewContructor(), productsTableView, listColumns);
    }

    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws AccessException, FilterException {
        return shoppingListController.requestIngredients(ingredientName);
    }

    @Override
    public Vector<IngredientWithQuantityFX> getIngredientsWithQuantityFX() {
        return new Vector<>(productsTableView.getItems());
    }

    @Override
    public void displayIngredients(Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector) {
        productsTableView.getItems().clear();
        productsTableView.setItems(FXCollections.observableArrayList(ingredientWithQuantityFXVector));
    }

    @Override
    public void cancelAndReturn() {
        shoppingListController.closeAddShoppingList();
    }

    /**
     * Create the shopping list
     * @param name the name of the shopping list that we want to create
     */
    @Override
    public void confirmAndReturn(final String name) throws AccessException, FilterException {
        shoppingListController.addNewShoppingList(name, this);
    }

    public void addShoppingListToTableView(final String name) {
        shoppingListTableView.getItems().add(new ShoppingList(name, new Vector<>()));
    }

    @FXML
    private void generateShoppingListFromMenu() throws IOException, AccessException, FilterException {
        final boolean anItemIsSelected = shoppingListTableView.getSelectionModel().getSelectedItem() != null;
        if (anItemIsSelected) {
            updateShoppingList(currentlySelectedShoppingList);
            shoppingListController.showGenerateShoppingListFromMenuView(this);
            shoppingListTableView.getSelectionModel().selectLast();
        }
    }

    @FXML
    private void exportToPDF() throws IOException {
        updateShoppingList(currentlySelectedShoppingList);
        if (currentShoppingListContainsItems()) {
            shoppingListController.exportToPDF(shoppingListTableView.getSelectionModel().getSelectedItem().getName());
            shoppingListController.displaySendEmail(currentlySelectedShoppingList.getName());
        }
        else
            AlertBoxGenerator.showWarning("La liste de course sélectionnée est vide !", "Veuillez ajouter un produit pour importer.");
    }

    private boolean currentShoppingListContainsItems(){
        return !currentlySelectedShoppingList.getIngredients().isEmpty();
    }

    @FXML
    public void displayQrCode() {
        updateShoppingList(currentlySelectedShoppingList);
        if (currentShoppingListContainsItems()) {
            shoppingListController.displayQrCode(shoppingListTableView.getSelectionModel().getSelectedItem().getName());
        }
        else
            AlertBoxGenerator.showWarning("La liste de course sélectionnée est vide !", "Veuillez ajouter un produit pour importer.");
    }

    /**
     * Interface for communicating with the shopping list controller
     */
    public interface DisplayShoppingListListener extends TableViewListener {
        void closeAddShoppingList();
        void addNewShoppingList(String newShoppingListName, DisplayShoppingListViewController displayShoppingListViewController) throws AccessException, FilterException;
        void returnToMainMenu() throws IOException;
        void showArchivedList() throws IOException, AccessException, FilterException;
        void deleteShoppingList(ShoppingList currentShoppingList);
        void updateShoppingListController(ShoppingList shoppingList);
        void showAddShoppingListView(DisplayShoppingListViewController displayShoppingListViewController) throws IOException;
        void showGenerateShoppingListFromMenuView(DisplayShoppingListViewController displayShoppingListViewController) throws IOException, AccessException, FilterException;
        Vector<Ingredient> requestIngredients(String ingredientName) throws FilterException, AccessException;
        Vector<String> getPossibleSuggestionsName() throws AccessException;
        Vector<String> getUnitNames(IngredientRecipe ingredientRecipe) throws AccessException, FilterException;
        void exportToPDF(String name);
        void displaySendEmail(String shoppingListName) throws IOException;
        void displayQrCode(String name);
    }
}
