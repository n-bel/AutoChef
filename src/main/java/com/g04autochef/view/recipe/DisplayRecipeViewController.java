package com.g04autochef.view.recipe;

import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.model.storableDAO.IngredientRecipe;
import com.g04autochef.model.storableDAO.Recipe;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.IngredientTableViewConstructor;
import com.g04autochef.view.tableConstructors.RecipeTableViewConstructor;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * View Controller for the recipes
 */
public final class DisplayRecipeViewController extends ViewController implements TableViewable<TableViewable.TableViewListener> {

    private DisplayRecipeListener recipeController;
    private Recipe currentRecipe;

    @FXML private Button deleteRecipeButton;
    @FXML private Button modifyRecipeButton;
    @FXML private javafx.scene.control.TableView<Recipe> recipeTableView;
    @FXML private TableColumn<Recipe, String> nameRecipeTableColumn;
    @FXML private TableView<IngredientRecipe> ingredientTableView;
    @FXML private TableColumn<IngredientRecipe, String> nameIngredientTableColumn;
    @FXML private TableColumn<IngredientRecipe, String> quantityIngredientTableColumn;
    @FXML private TableColumn<IngredientRecipe, String> unitIngredientTableColumn;
    @FXML private TableColumn<IngredientRecipe, String> categoryIngredientTableColumn;
    @FXML private ListView<String> instructionListView;
    @FXML private Label labelNameRecipe;
    @FXML private Label labelCookingStyle;
    @FXML private Label labelCategory;
    @FXML private Label labelNbPerson;
    @FXML private ImageView recipeImageView;

    /**
     * @param recipeController the reference to the recipe controller
     */
    public void setListener(DisplayRecipeListener recipeController) {
        this.recipeController = recipeController;
    }

    /**
     * Initialize the buttons and the listener for knowing which product we are selecting
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (deleteRecipeButton != null) deleteRecipeButton.setDisable(true);
        if (modifyRecipeButton != null) modifyRecipeButton.setDisable(false);
        if (recipeTableView != null)
            recipeTableView.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
                if (newValue != null){
                    deleteRecipeButton.setDisable(false);
                    modifyRecipeButton.setDisable(false);
                    ingredientTableView.getItems().clear();
                    instructionListView.getItems().clear();
                    displayRecipe(newValue);
                    updateImage(newValue);
                    currentRecipe = newValue;
                }else{
                    deleteRecipeButton.setDisable(true);
                    modifyRecipeButton.setDisable(true);
                }
            });
    }

    /**
     * Action of the return button
     */
    @FXML private void returnMainMenu() throws IOException {
        recipeController.returnToMainMenu();
    }

    @FXML
    private void createNewRecipe() throws IOException, AccessException {
        recipeController.createNewRecipe();
    }

    @FXML
    private void modifySelectedRecipe() throws IOException, AccessException {
        recipeController.modifyRecipe(currentRecipe);
    }

    @FXML
    private void deleteSelectedRecipe() {
        if (currentRecipe != null) { deleteRecipe(); }
        if (recipeTableView.getItems().isEmpty()) { deleteRecipeInView(); }
    }

    private void deleteRecipe(){
        recipeController.deleteSelectedRecipe(currentRecipe);
        recipeTableView.getItems().remove(currentRecipe);
    }

    private void deleteRecipeInView(){
        ingredientTableView.getItems().clear();
        instructionListView.getItems().clear();
    }

    /**
     * Load the recipes into the tables views
     */
    public void loadAllRecipes(final ObservableList<Recipe> recipes){
        recipeTableView.setItems(recipes);
        recipeTableView.getSelectionModel().selectFirst();
    }

    /**
     * Create the recipe table view with its column according to its attributes
     */
    public void definitionTableViewRecipe() {
        Vector<TableColumn<?, ?>> columnRecipeList = new Vector<>();
        columnRecipeList.add(nameRecipeTableColumn);
        defineTableView(new RecipeTableViewConstructor(), recipeTableView, columnRecipeList);
    }

    /**
     * Initialize the table view for displaying the recipe and its products
     */
    public void displayRecipe(final Recipe recipe) {
        addIngredientsToTableViewForRecipe(recipe);
        addInstructionsToTableViewForRecipe(recipe);
        setLabelsForRecipe(recipe);
    }

    private void addInstructionsToTableViewForRecipe(final Recipe recipe){
        for (String elem: recipe.getInstructions()){
            instructionListView.getItems().add(elem);
        }
    }

    private void addIngredientsToTableViewForRecipe(final Recipe recipe){
        ObservableList<IngredientRecipe> ingredientList = FXCollections.observableArrayList();
        ingredientList.addAll(recipe.getIngredients());
        ingredientTableView.setItems(ingredientList);
    }

    private void setLabelsForRecipe(final Recipe recipe){
        labelNameRecipe.setText(recipe.getName());
        labelCategory.setText(recipe.getType().getType());
        labelNbPerson.setText((String.valueOf(recipe.getPeople())));
        labelCookingStyle.setText(recipe.getCookingStyle().style());
    }

    public void defineIngredientTableView() {
        Vector<TableColumn<?, ?>> listColumns = new Vector<>(Arrays.asList(nameIngredientTableColumn, categoryIngredientTableColumn, quantityIngredientTableColumn,
                unitIngredientTableColumn));
        defineTableView(new IngredientTableViewConstructor(), ingredientTableView, listColumns);
    }

    private void updateImage(Recipe recipe) {
        Image image = recipeController.getImage(recipe);
        setImage(image);

    }

    public void setImage(Image image) {
        recipeImageView.setImage(image);
        recipeImageView.setPreserveRatio(false);
    }

    /**
     * Listener for communicating with the RecipeController
     */
    public interface DisplayRecipeListener extends TableViewListener {
        void returnToMainMenu() throws IOException;
        void createNewRecipe() throws IOException, AccessException;
        void deleteSelectedRecipe(Recipe recipe);
        void modifyRecipe(Recipe selectedRecipe) throws IOException, AccessException;
        Image getImage(Recipe recipe);
    }
}


