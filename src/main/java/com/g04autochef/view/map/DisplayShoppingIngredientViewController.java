package com.g04autochef.view.map;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.FXComponents.IngredientPriceFX;
import com.g04autochef.view.FXComponents.IngredientWithQuantityFX;
import com.g04autochef.view.IntegerSpinner;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.IngredientPriceTableViewConstructor;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;

public class DisplayShoppingIngredientViewController extends ViewController implements TableViewable<TableViewable.TableViewListener>, IngredientWithQuantityFX.IngredientWithQuantityFXListener {

    DisplayShoppingIngredientListener shoppingIngredientController;

    private Shop selectedShop;

    @FXML private Label nameShopLabel;
    @FXML private TableView<IngredientPriceFX> ingredientTableView;
    @FXML private TableColumn<IngredientPriceFX, TextField> nameTableColumn;
    @FXML private TableColumn<IngredientPriceFX, String> categoryTableColumn;
    @FXML private TableColumn<IngredientPriceFX, IntegerSpinner> quantityTableColumn;
    @FXML private TableColumn<IngredientPriceFX, ComboBox<String>> unitTableColumn;
    @FXML private TableColumn<IngredientPriceFX, Spinner<Double>> priceTableColumn;
    @FXML private TableColumn<IngredientPriceFX, Button>deleteTableColumn;

    /**
     * @param mapController reference to the shopping list controller
     */
    public void setListener(DisplayShoppingIngredientListener mapController){
        this.shoppingIngredientController = mapController;
    }

    @SuppressWarnings("unused")
    @FXML
    private void addIngredient() throws AccessException {
        Vector<String> possibleSuggestionsName = shoppingIngredientController.getPossibleSuggestionsName();

        IngredientPriceFX ingredientPriceFX = new IngredientPriceFX(possibleSuggestionsName, this);
        ingredientTableView.getItems().add(ingredientPriceFX);
        ingredientTableView.getSelectionModel().selectLast();
    }

    public void setShopName(String name) {
        nameShopLabel.setText(name);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws AccessException, FilterException {
        return shoppingIngredientController.requestIngredients(ingredientName);
    }

    /**
     * @throws IOException
     * this method will add the ingredient price into the shop and will return to the map
     */
    @SuppressWarnings("unused")
    @FXML
    private void returnToMap() throws IOException {
        if (tableIsCorrect()) {
            Vector<IngredientPrice> ingredientsPrice = new Vector<>();
            for (IngredientPriceFX ingredientPriceFX : ingredientTableView.getItems()) {
                Vector<Unit> unitsUnit = new Vector<>();
                for (String unitString : ingredientPriceFX.getUnitName().getItems())
                    unitsUnit.add(new Unit(unitString));
                ingredientsPrice.add(new IngredientPrice(ingredientPriceFX.getPrice().getValue(),
                        ingredientPriceFX.getName().getText(),
                        new IngredientWithQuantity(new Ingredient(ingredientPriceFX.getName().getText(),
                                new IngredientType(ingredientPriceFX.getType()), unitsUnit),
                                new Unit(ingredientPriceFX.getUnitName().getValue()), ingredientPriceFX.getQuantity().getValue())));
            }
            selectedShop = new Shop(selectedShop, ingredientsPrice);
            shoppingIngredientController.saveShopChanges(selectedShop);
            shoppingIngredientController.returnToMap();
        }
        else
            AlertBoxGenerator.showWarning("Une erreur est présente dans la liste d'ingrédient", "Veuillez la corriger pour continuer");
    }

    private boolean tableIsCorrect() {
        for (IngredientPriceFX ingredientPriceFX : ingredientTableView.getItems())
            if (!ingredientPriceFX.nameIsValid())
                return false;
        return true;
    }

    @Override
    public Vector<IngredientWithQuantityFX> getIngredientsWithQuantityFX() {
        return new Vector<>(ingredientTableView.getItems());
    }

    @Override
    public void displayIngredients(Vector<IngredientWithQuantityFX> ingredientFXVector) throws AccessException {
        ingredientTableView.getItems().clear();
        for (IngredientWithQuantityFX ingredientrecipeFX: ingredientFXVector){

            final IngredientPrice ingredientPrice = new IngredientPrice(
                    ((IngredientPriceFX) ingredientrecipeFX).getPrice().getValue(),
                    ingredientrecipeFX.getName().getText(), ingredientrecipeFX.getIngredientWithQuantity());

            final IngredientPriceFX ingredientPriceFX = new IngredientPriceFX(ingredientPrice,  shoppingIngredientController.getPossibleSuggestionsName(), this);
            ingredientTableView.getItems().add(ingredientPriceFX);
        }
    }

    public void setShop(Shop selectedShop) {
        this.selectedShop = selectedShop;
    }

    public void initializeShopView() {
        this.setShopName(this.selectedShop.getName());
    }

    public void definitionTableViewIngredientPrice() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(Arrays.asList(nameTableColumn, categoryTableColumn, quantityTableColumn,
                unitTableColumn, deleteTableColumn, priceTableColumn));
        defineTableView(new IngredientPriceTableViewConstructor(), ingredientTableView, listColumns);
    }

    public void displayIngredientPrices() throws AccessException {
        Vector<IngredientPrice> ingredientPricesVector = this.selectedShop.getIngredients();
        Vector<IngredientPriceFX> ingredientPriceFXVector = new Vector<>();
        for(IngredientPrice ingredientPrice: ingredientPricesVector){
            IngredientPriceFX ingredientPriceFX = new IngredientPriceFX(ingredientPrice, shoppingIngredientController.getPossibleSuggestionsName(), this);
            ingredientPriceFXVector.add(ingredientPriceFX);
        }
        ObservableList<IngredientPriceFX> ingredients = FXCollections.observableList(ingredientPriceFXVector);
        ingredientTableView.setItems(ingredients);
    }

    /**
     * interface for communicating with the map controller
     */
    public interface DisplayShoppingIngredientListener extends TableViewListener {
        void returnToMap() throws IOException;
        Vector<Ingredient> requestIngredients(String ingredientName) throws FilterException, AccessException;
        Vector<String> getPossibleSuggestionsName() throws AccessException;
        void saveShopChanges(Shop shop);
    }
}

