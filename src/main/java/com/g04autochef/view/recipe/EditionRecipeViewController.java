package com.g04autochef.view.recipe;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.FXComponents.IngredientWithQuantityFX;
import com.g04autochef.view.FXComponents.InstructionFX;
import com.g04autochef.view.IntegerSpinner;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.IngredientTableViewConstructor;
import com.g04autochef.view.tableConstructors.InstructionTableViewConstructor;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Controller View for the edition of recipes
 */
public class EditionRecipeViewController extends ViewController implements TableViewable<TableViewable.TableViewListener> , IngredientWithQuantityFX.IngredientWithQuantityFXListener {

    private EditionRecipeListener recipeController;

    @SuppressWarnings("unused")
    private InstructionFX currentInstruction;

    @FXML private TextField nameTextField;
    @FXML private ChoiceBox<String> cookingStyleChoiceBox;
    @FXML private Spinner<Integer> nbPersonSpinner;
    @FXML private RadioButton vegeRadioButton;
    @FXML private RadioButton meatRadioButton;
    @FXML private RadioButton fishRadioButton;
    @FXML private RadioButton otherRadioButton;
    @FXML private Button addRecipeButton;
    @FXML private Button importJSONButton;
    @FXML private ToggleGroup groupCategory;
    @FXML private TableView<IngredientWithQuantityFX> ingredientTableView;
    @FXML private TableColumn<IngredientWithQuantityFX, TextField> nameIngredientTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, String> categoryIngredientTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, IntegerSpinner> quantityIngredientTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, ComboBox<String>> unitIngredientTableColumn;
    @FXML private TableColumn<IngredientWithQuantityFX, Button> deleteTableColumn;
    @FXML private TableView<InstructionFX> instructionTableView;
    @FXML private TableColumn<InstructionFX, TextField> instructionTableColumn;
    @FXML private ImageView recipeImageView;

    /**
     * Initialize the listeners for knowing which product and instruction we are selecting
     */
    public void setListener(EditionRecipeListener recipeController) {
        this.recipeController = recipeController;
        instructionTableView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
        if (newValue != null) {
            currentInstruction = newValue;
        }});
    }


    /**
     * Initialize the buttons and the listeners
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        definitionNbPersonSpinner();
    }

    /**
     * Convert an Instruction String to an Instruction FX Component
     * @param instructionsString vector of instructions
     * @return Vector of InstructionFX (a FX Component)
     */
    private Vector<InstructionFX> instructionsStringToInstructionFX(Vector<String> instructionsString) {
        Vector<InstructionFX> instructionsFX = new Vector<>();
        for (String instruction : instructionsString) instructionsFX.add(new InstructionFX(instruction));
        return instructionsFX;
    }

    /**
     * Initialize all the view component according to the selectedRecipe
     * @param selectedRecipe the recipe we want to modify
     */
    public void initializeSelectedRecipe(Recipe selectedRecipe) throws AccessException {
        nameTextField.setText(selectedRecipe.getName());
        nbPersonSpinner.getValueFactory().setValue(selectedRecipe.getPeople());
        setCookingStyleChoiceBox(selectedRecipe);

        final Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector = makeFXComponentsFromSelectedRecipe(selectedRecipe);
        ingredientTableView.setItems(FXCollections.observableArrayList(ingredientWithQuantityFXVector));

        Vector<InstructionFX> instructionsFX = instructionsStringToInstructionFX(selectedRecipe.getInstructions());
        instructionTableView.setItems(FXCollections.observableArrayList(instructionsFX));

        setTypeRadioButtonSelection(selectedRecipe);
        conditionallyEnableAddRecipeButton();
    }

    private void setTypeRadioButtonSelection(Recipe selectedRecipe) {
        final String type = selectedRecipe.getType().getType();
        switch (type) {
            case "Végétarien" -> vegeRadioButton.setSelected(true);
            case "Viande" -> meatRadioButton.setSelected(true);
            case "Poisson" -> fishRadioButton.setSelected(true);
            default -> otherRadioButton.setSelected(true);
        }
    }

    private void setCookingStyleChoiceBox(Recipe selectedRecipe) throws AccessException {
        Vector<String> possibleSuggestionsTypes = recipeController.getPossibleSuggestionsTypes();
        if (possibleSuggestionsTypes.contains(selectedRecipe.getCookingStyle().style())) {
            cookingStyleChoiceBox.setValue(selectedRecipe.getCookingStyle().style());
        }
    }

    private Vector<IngredientWithQuantityFX> makeFXComponentsFromSelectedRecipe(final Recipe selectedRecipe) throws AccessException {
        Vector<String> possibleSuggestionsName = recipeController.getPossibleSuggestionsName();
        Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector = new Vector<>();
        Vector<IngredientWithQuantity> ingredientWithQuantitiesVector = selectedRecipe.getIngredientsWithQuantites();
        for (IngredientWithQuantity ingredientWithQuantity: ingredientWithQuantitiesVector){
            IngredientWithQuantityFX ingredientWithQuantityFX = new IngredientWithQuantityFX(ingredientWithQuantity, possibleSuggestionsName, this);
            ingredientWithQuantityFXVector.add(ingredientWithQuantityFX);
        }
        return ingredientWithQuantityFXVector;
    }


    /**
     * Initialize the text field of cooking style
     */
    public void initializeCookingStyle() throws AccessException {
        Vector<String> possibleSuggestionsTypes = recipeController.getPossibleSuggestionsTypes();
        cookingStyleChoiceBox.setItems(FXCollections.observableList(possibleSuggestionsTypes));
        cookingStyleChoiceBox.getSelectionModel().selectFirst();
    }

    private void definitionNbPersonSpinner() {
        final int initialValue = 1;
        final int min = 1;
        final int max = 999;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        nbPersonSpinner.setValueFactory(valueFactory);
    }

    /**
     * Action for the return button
     */
    @FXML
    private void returnToRecipeDisplay() {
       recipeController.returnToRecipeDisplay();
    }

    /**
     * Action for the json import button
     */
    @FXML
    private void importJSON() {
        FileChooser fileExplorer = new FileChooser();
        fileExplorer.setTitle("Import fichier JSON");
        fileExplorer.getExtensionFilters().add(new FileChooser.ExtensionFilter("fichier JSON", "*.json"));
        File selectedFile = fileExplorer.showOpenDialog(new Stage());
        if (selectedFile != null)
            recipeController.importJSON(selectedFile.getAbsolutePath());
    }

    /**
     * Action of adding a new ingredient on the ingredient table view
     */
    @SuppressWarnings("unused")
    @FXML
    private void addIngredient() throws AccessException {
        Vector<String> possibleSuggestionsName = recipeController.getPossibleSuggestionsName();
        IngredientWithQuantityFX ingredientFX = new IngredientWithQuantityFX(possibleSuggestionsName, this);
        ingredientTableView.getItems().add(ingredientFX);
        ingredientTableView.getSelectionModel().selectLast();
        conditionallyEnableAddRecipeButton();
    }

    /**
     * Action for adding an instruction to the list view
     */
    @FXML
    private void addInstruction() {
        instructionTableView.getItems().add(new InstructionFX());
        conditionallyEnableAddRecipeButton();
    }

    @FXML
    private void conditionallyEnableAddRecipeButton() {
        final boolean nameTextFieldIsBlank = nameTextField.getText().isBlank();
        final boolean groupCategoryIsNull = groupCategory.getSelectedToggle() == null;
        final boolean ingredientIsEmpty = ingredientTableView.getItems().isEmpty();
        final boolean instructionIsEmpty = instructionTableView.getItems().isEmpty();
        addRecipeButton.setDisable(nameTextFieldIsBlank || groupCategoryIsNull || ingredientIsEmpty || instructionIsEmpty);
    }

    /**
     * Called during typing, updates state of buttons accordingly.
     */
    @FXML
    private void updateElementStatesDuringTyping() {
        conditionallyEnableAddRecipeButton();
    }

    @FXML
    private void uploadImage(){
        recipeController.uploadImage();
    }

    public void setImageRecipe(Image image) {
        recipeImageView.setImage(image);
        recipeImageView.setPreserveRatio(false);
    }

    /**
     * Checks if the name of an ingredient is valid
     */
    private void verifyIngredientsValidity() throws IllegalArgumentException{
        for (IngredientWithQuantityFX ingredientWithQuantityFX : ingredientTableView.getItems()){
            if (!ingredientWithQuantityFX.nameIsValid())
                throw new IllegalArgumentException();
        }
    }


    /**
     * @param instructionsFX instructionFX object we want to convert
     * {@return Convert the FX object to String}
     */
     private Vector<String> instructionInstructionFXToString(final ObservableList<InstructionFX> instructionsFX) {
        Vector<String> instructionsString = new Vector<>();
        for (InstructionFX instructionFX : instructionsFX) instructionsString.add(instructionFX.getInstructionString());
        return instructionsString;
    }

    /**
     * {@return Recipe created depending on the components of the view}
     */
    private Recipe buildRecipe() throws IllegalArgumentException {
        verifyIngredientsValidity(); // Throws IllegalArgumentException
        cleanInstructions();
        final String category = getCategoryFromRadioButton();
        Vector<String> instructionsString = instructionInstructionFXToString(instructionTableView.getItems());
        final Vector<IngredientRecipe> ingredientRecipesVector = makeIngredientRecipeFromFXElements();
        return  new Recipe(ingredientRecipesVector, new Vector<>(instructionsString), nbPersonSpinner.getValue(),
                nameTextField.getText(), new RecipeType(category), new RecipeCookingStyle(cookingStyleChoiceBox.getValue()));
    }

    private Vector<IngredientRecipe> makeIngredientRecipeFromFXElements(){
        final Vector<IngredientWithQuantityFX> product = new Vector<>(ingredientTableView.getItems());
        final Vector<IngredientRecipe> ingredientRecipesVector = new Vector<>();
        for (IngredientWithQuantityFX ingredientWithQuantityFX : product){
            ingredientRecipesVector.add(new IngredientRecipe(ingredientWithQuantityFX.FXToIngredientQuantity()));
        }
        return ingredientRecipesVector;
    }

    private String getCategoryFromRadioButton() {
        final String category;
        if (vegeRadioButton.isSelected())
            category = "Végétarien";
        else if (meatRadioButton.isSelected())
            category = "Viande";
        else if ((fishRadioButton.isSelected()))
            category = "Poisson";
        else
            category = "Autre";
        return category;
    }

    @FXML
    private void createAndAddRecipe() {
        try {
            Recipe recipe = buildRecipe();
            recipeController.addRecipe(recipe);
        }
        catch (IllegalArgumentException e){
            AlertBoxGenerator.showWarning("Un ingrédient est invalide !", "Veuillez le remplir");
        }
    }

    /**
     * Remove all the instructions rows that are not completed
     */
    private void cleanInstructions() {
        instructionTableView.getItems().removeIf(instructionFX -> instructionFX.getInstructionString().isBlank());
    }


    public void definitionTableViewProduct() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(Arrays.asList(nameIngredientTableColumn, categoryIngredientTableColumn,
                quantityIngredientTableColumn, unitIngredientTableColumn, deleteTableColumn));
        defineTableView(new IngredientTableViewConstructor(), ingredientTableView,
                listColumns);
    }

    public void definitionTableViewInstruction() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(List.of(instructionTableColumn));
        defineTableView(new InstructionTableViewConstructor(), instructionTableView, listColumns);
    }

    /**
     * Ask the controller to request all the ingredients
     * @return Vector of ingredients
     */
    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws AccessException, FilterException {
        return recipeController.requestIngredients(ingredientName);
    }

    /**
     * Delete the ingredient from the table view
     * @param ingredientFX ingredient we want to delete
     */
    @Override
    public void deleteIngredient(final IngredientWithQuantityFX ingredientFX) throws AccessException {
        IngredientWithQuantityFX.IngredientWithQuantityFXListener.super.deleteIngredient(ingredientFX);
        conditionallyEnableAddRecipeButton();
    }

    @Override
    public Vector<IngredientWithQuantityFX> getIngredientsWithQuantityFX() {
        return new Vector<>(ingredientTableView.getItems());
    }

    @Override
    public void displayIngredients(final Vector<IngredientWithQuantityFX> ingredientWithQuantityFXVector) {
        ingredientTableView.getItems().clear();
        ingredientTableView.setItems(FXCollections.observableArrayList(ingredientWithQuantityFXVector));
    }

    public void setEditMode() {
        nameTextField.setEditable(false);
        addRecipeButton.setOnAction(e -> updateRecipe());
        importJSONButton.setDisable(true);
        importJSONButton.setOpacity(0);
    }

    private void updateRecipe() {
        try {
            Recipe recipe = buildRecipe();
            recipeController.updateRecipe(recipe);
        }
        catch (IllegalArgumentException e){
            AlertBoxGenerator.showWarning("Un ingrédient est invalide !", "Veuillez le remplir");
        }
    }

    /**
     * Listener in order to have a reference to the RecipeController
     */
    public interface EditionRecipeListener extends TableViewListener {
        void returnToRecipeDisplay();
        void importJSON(String path);
        void addRecipe(Recipe recipe);
        void updateRecipe(Recipe recipe);
        Vector<String> getPossibleSuggestionsName() throws AccessException;
        Vector<Ingredient> requestIngredients(String ingredientName) throws FilterException, AccessException;
        Vector<String> getPossibleSuggestionsTypes() throws AccessException;
        void uploadImage();
    }
}
