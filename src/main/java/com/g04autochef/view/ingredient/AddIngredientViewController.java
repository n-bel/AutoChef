package com.g04autochef.view.ingredient;

import com.g04autochef.model.storableDAO.IngredientType;
import com.g04autochef.model.storableDAO.Unit;
import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * View controller that let the user create Ingredients
 * Communicate with the AddIngredientController which implements the AddIngredientViewListener interface
 */
public class AddIngredientViewController extends ViewController {

    @FXML private Button addButton;
    @FXML private TextField ingredientName;
    @FXML private ComboBox<String> dishType;
    @FXML private CheckBox box1, box2, box3, box4, box5, box6, box7, box8, box9, box10;
    private Vector<CheckBox> vecBoxes;
    private AddIngredientViewListener addIngredientController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * Set the listener to communicate with AddIngredientController
     */
    public void setListener(AddIngredientViewListener addIngredientController) {
        this.addIngredientController = addIngredientController;
    }

    /**
     * Set values in the type combobox
     * @param vec vec of existing types
     */
    public void setTypes(Vector<IngredientType> vec) {
        for (IngredientType type : vec) {
            dishType.getItems().add(type.type());
        }
        dishType.getSelectionModel().selectFirst();
    }

    /**
     * Set the units to the checkboxes
     * @param vecUnite name of the existing units
     */
    public void setBoxesValues(Vector<Unit> vecUnite) {
        vecBoxes = new Vector<>(Arrays.asList(box1, box2, box3, box4, box5, box6, box7, box8, box9, box10));
        for (int i = 0; i < (Math.min(vecBoxes.size(), vecUnite.size())); i++) {
            vecBoxes.get(i).setDisable(false);
            vecBoxes.get(i).setOpacity(1);
            vecBoxes.get(i).setText(vecUnite.get(i).getName());
        }
    }

    /**
     * Tell the AddIngredientController to try to add the ingredient to the storage
     */
    @SuppressWarnings("unused")
    @FXML
    private void addIngredient() {
        Vector<String> vecUnitString = new Vector<>();
        String type = dishType.getValue();
        for (CheckBox vecBox : vecBoxes) {
            if (vecBox.isSelected()) {
                vecUnitString.add(vecBox.getText());
            }
        }
        addIngredientController.addIngredient(ingredientName.getText(), type, vecUnitString);
    }

    /**
     * Tell the controller values have changed
     */
    @FXML
    private void valueChanged() {
        addIngredientController.valueChanged(ingredientName.getText(), vecBoxes);
    }

    /**
     * Enable or disable the add button
     * Uses !enable as button only has a setDisable functionality
     */
    public void enableAddButton(boolean enabled) {
        addButton.setDisable(!enabled);
    }

    /**
     * Reset values to show the default view
     */
    public void reset() {
        ingredientName.setText("");
        for (CheckBox box : vecBoxes) {
            box.selectedProperty().setValue(false);
        }
        dishType.getSelectionModel().selectFirst();
    }

    /**
     * Return to the main menu
     */
    @SuppressWarnings("unused")
    @FXML private void returnToMainMenu() {
        addIngredientController.returnToMainMenu();
    }

    /**
     * The listener of AddIngredientViewController in order to communicate with the AddIngredientController
     */
    public interface AddIngredientViewListener{
        void returnToMainMenu();
        void addIngredient(String name, String type, Vector<String> vecUnites);
        void valueChanged(String name, Vector<CheckBox> checkboxes);
    }
}