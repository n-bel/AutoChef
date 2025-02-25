package com.g04autochef.view.FXComponents;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.IntegerSpinner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.textfield.TextFields;


import java.util.Vector;

/**
 * Class that will construct an Interactive View for editing an Ingredient
 */
public class IngredientWithQuantityFX {

    private final TextField name;
    private final Vector<String> possibleSuggestionsName;
    private String type;
    private final IntegerSpinner quantity;
    private final ComboBox<String> unit;
    private Button delete;

    private final IngredientWithQuantityFXListener controller;

    public IngredientWithQuantityFX(Vector<String> possibleSuggestionsName, IngredientWithQuantityFXListener controller) {
        this.name = new TextField();
        updateNameStyle();

        this.possibleSuggestionsName = possibleSuggestionsName;
        TextFields.bindAutoCompletion(this.name, this.possibleSuggestionsName);

        this.type = "";
        this.quantity = new IntegerSpinner(0, Integer.MAX_VALUE, 1, 1);
        this.quantity.setEditable(true);

        this.unit = new ComboBox<>();
        initialiseUnitComboBox();

        this.controller = controller;

        initDeleteButton();

        this.initListener();
    }

    public IngredientWithQuantityFX(String name, Vector<String> possibleSuggestionsName, String category, Integer quantity,
                                    String unit, ObservableList<String> possibleUnits, IngredientWithQuantityFXListener controller) {

        this(possibleSuggestionsName, controller);

        this.name.setText(name);
        updateNameStyle();

        this.type = category;

        this.quantity.getValueFactory().setValue(quantity);

        this.unit.setItems(possibleUnits);
        this.unit.getSelectionModel().selectFirst();
        if (possibleUnits.contains(unit))
            this.unit.setValue(unit);

    }

    public IngredientWithQuantityFX(IngredientWithQuantity ingredientWithQuantity, Vector<String> possibleSuggestionsName,
                                    IngredientWithQuantityFXListener controller) {
        this(ingredientWithQuantity.getName(), possibleSuggestionsName, ingredientWithQuantity.getType(),
                ingredientWithQuantity.getQuantity(), ingredientWithQuantity.getUnitName(),
                controller.unitsToString(ingredientWithQuantity), controller);
    }

    /**
     * Initialize the delete button
     */
    private void initDeleteButton() {
        final int PREF_WIDTH_IMG = 15;
        final int PREF_HEIGHT_IMG = 15;
        this.delete = new Button("Supprimer");
        this.delete.getStyleClass().add("button-delete");
        ImageView deleteImage = new ImageView(new Image("com/g04autochef/img/Garbage-Closed-256.png"));
        deleteImage.setFitHeight(PREF_HEIGHT_IMG);
        deleteImage.setFitWidth(PREF_WIDTH_IMG);
        this.delete.setGraphic(deleteImage);
        this.delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.delete.setOnAction(e -> deleteIngredient());
    }

    private void initialiseUnitComboBox() {
        final double PREF_WIDTH = 140.0;
        final double PREF_HEIGHT = 30.0;
        this.unit.setPrefSize(PREF_WIDTH, PREF_HEIGHT);
    }

    protected void deleteIngredient(){
        try {
            controller.deleteIngredient(this);
        } catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur lors de la suppréssion d'un ingrédient", e);
        }
    }

    /**
     * Convert the instance (this) of to an IngredientWithQuantity
     * @return an IngredientWithQuantity object created from IngredientWithQuantityFX
     */
    public IngredientWithQuantity FXToIngredientQuantity(){
        Vector<Unit> unitVector = new Vector<>();
        for (String s: unit.getItems())
            unitVector.add(new Unit(s));
        final Ingredient ingredient = new Ingredient(name.getText(), new IngredientType(type),unitVector);
        return new IngredientWithQuantity(ingredient, new Unit(unit.getValue()), quantity.getValue());
    }

    public TextField getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public IntegerSpinner getQuantity() {
        return quantity;
    }

    public ComboBox<String> getUnitName() {
        return unit;
    }

    @SuppressWarnings("unused")
    public Button getDelete() {
        return delete;
    }

    public IngredientWithQuantity getIngredientWithQuantity(){
        final Vector<Unit> unitVector = getUnitsFromComboBox();
        final Ingredient ingredient = new Ingredient(this.name.getText(), new IngredientType(this.type), unitVector);
        return new IngredientWithQuantity(ingredient, new Unit(this.unit.getValue()), this.quantity.getValue());
    }

    private Vector<Unit> getUnitsFromComboBox() {
        final Vector<Unit> unitVector = new Vector<>();
        for (final String unitName: this.unit.getItems()){
            unitVector.add(new Unit(unitName));
        }
        return unitVector;
    }

    /**
     * Filter the names and see if the characters inside the text field is a valid name
     * @return boolean for knowing if a name is valid
     */
    public boolean nameIsValid() {
        if (!name.getText().isBlank())
            return possibleSuggestionsName.contains(name.getText());
        return false;
    }

    /**
     * Highlight the edges of the textfield if the name typed is wrong
     */
    private void updateNameStyle() {
        if (nameIsValid()) {
            this.name.setStyle("-fx-border-color: LIGHTSTEELBLUE;");
        }
        else {
            this.name.setStyle("-fx-border-color: RED;");
        }
    }

    /**
     * Initialize a listener for an interactive name textfield
     */
    private void initListener(){
        this.name.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !oldText.equals(newText)) {
                try {
                    if (nameIsValid()) {
                        updateProduct();
                    }
                    updateNameStyle();
                } catch (FilterException | AccessException e) {
                   AlertBoxGenerator.showError("Erreur lors de l'initialisation du textield interactive", e);
                }
            }
        });
    }


    /**
     * Update the product (category and unit)
     */
    protected void updateProduct() throws FilterException, AccessException {
        final Vector<Ingredient> vectorIngredient = controller.requestIngredients(this.name.getText());
        final Ingredient ingredient = vectorIngredient.get(0);
        this.type = ingredient.getType().type();
        final Vector<String> unitsNames = ingredient.getUnitNames();
        this.unit.setItems(FXCollections.observableList(unitsNames));
        this.unit.getSelectionModel().selectFirst();
        controller.updateToView(this);
    }

    /**
     * Interface that will allow the communication with the controllers that use this interactive IngredientRecipe
     */
    public interface IngredientWithQuantityFXListener {
        Vector<Ingredient> requestIngredients(String ingredientName) throws AccessException, FilterException;
        Vector<IngredientWithQuantityFX> getIngredientsWithQuantityFX();
        void displayIngredients(final Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector) throws AccessException;

        /**
         * Remove the ingredient from the table view
         * @param ingredientFX ingredientRecipeFX we want to delete
         */
        default void deleteIngredient(final IngredientWithQuantityFX ingredientFX) throws AccessException {
            final Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector = getIngredientsWithQuantityFX();
            for (int i = 0; i < ingredientWithQuantityFXVector.size(); ++i){
                final IngredientWithQuantityFX ingredientWithQuantityFX = ingredientWithQuantityFXVector.get(i);
                final boolean ingredientNameCorresponds = ingredientWithQuantityFX.getName().getText().equals(ingredientFX.getName().getText());
                final boolean ingredientTypeCorresponds = ingredientWithQuantityFX.getType().equals(ingredientFX.getType());
                final boolean ingredientValueFactoryCorresponds = ingredientWithQuantityFX.getQuantity().getValueFactory().getValue().equals(ingredientFX.getQuantity().getValueFactory().getValue());
                if ( ingredientNameCorresponds && ingredientTypeCorresponds && ingredientValueFactoryCorresponds) {
                    ingredientWithQuantityFXVector.remove(i);
                    break;
                }
            }
            updateDisplay(ingredientWithQuantityFXVector);
        }

        private void updateDisplay(final Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector) throws AccessException {
            this.displayIngredients(ingredientWithQuantityFXVector);
        }

        /**
         * Update by replacing the old ingredient by the new one and set it to the table view
         * @param newIngredientWithQuantityFX new ingredient recipe
         */
        default void updateToView(final IngredientWithQuantityFX newIngredientWithQuantityFX) throws AccessException {
            boolean isValid = true;
            Vector<IngredientWithQuantityFX> ingredientsWithQuantityFXVector = getIngredientsWithQuantityFX();
            for (IngredientWithQuantityFX ingredientWithQuantityFX : ingredientsWithQuantityFXVector){
                final boolean nameCorresponds = ingredientWithQuantityFX.getName().getText().equals(newIngredientWithQuantityFX.getName().getText());
                if ( nameCorresponds && ingredientWithQuantityFX != newIngredientWithQuantityFX){
                    isValid = false;
                    break;
                }
            }
            if(isValid){
                this.displayIngredients(ingredientsWithQuantityFXVector);
            }
            else{
                AlertBoxGenerator.showWarning("Doublons d'ingredients !", "Les doublons ne sont pas permis. Suppression du doublon...");
                newIngredientWithQuantityFX.getName().clear();
            }
        }
        /**
         * @param ingredientRecipe that we will extact its units
         * {@return Returns the vector of all the units available for the ingredientRecipe}
         */
        default ObservableList<String> unitsToString(final IngredientWithQuantity ingredientRecipe) {
            Vector<String> unitsNameVector = ingredientRecipe.getIngredient().getUnitNames();
            final ObservableList<String> unitsNameObservableList = FXCollections.observableList(unitsNameVector);
            return unitsNameObservableList;
        }
    }
}
