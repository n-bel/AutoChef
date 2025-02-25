package com.g04autochef.controller;

import com.g04autochef.controller.utils.ImageController;
import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.Picturable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.ingredientFields.IngredientFieldName;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.controller.utils.RecipeJSONReader;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.Windows;
import com.g04autochef.view.recipe.DisplayRecipeViewController;
import com.g04autochef.view.recipe.EditionRecipeViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Vector;

/**
 * Controller for all the recipes view (display and edition)
 */
public class RecipeController<RecipeDAO extends DAO<Recipe> & Updatable<Recipe> & Picturable<Recipe>, RecipeCookingStyleDAO extends DAO<RecipeCookingStyle>, IngredientDAO extends DAO<Ingredient>> extends Controller implements DisplayRecipeViewController.DisplayRecipeListener, EditionRecipeViewController.EditionRecipeListener {

    private final Stage stage;
    private final RecipeDAO recipeDAO;
    private final RecipeCookingStyleDAO recipeCookingDAO;
    private final IngredientDAO ingredientDAO;
    private EditionRecipeViewController editionRecipeViewController;
    private final ImageController<Recipe, RecipeDAO> imageController;
    private File file;

    /**
     * @param stage Window will contain the scene
     * @param mainController Reference to the MainController
     */
    public RecipeController(Stage stage, ControllerListener mainController, RecipeDAO recipeDAO, RecipeCookingStyleDAO recipeTypeDAO, IngredientDAO ingredientDAO) {
        super(mainController);
        this.stage = stage;
        this.recipeDAO = recipeDAO;
        this.recipeCookingDAO = recipeTypeDAO;
        this.ingredientDAO = ingredientDAO;
        imageController = new ImageController<>(recipeDAO);
    }

    /**
     * Method overriden from Controller
     * will load the View and show it
     */
    @Override
    public void show() {
        try {
            FXMLLoader loader = getFxmlLoader(stage, DisplayRecipeViewController.class.getResource(Windows.RecipeList.getPathToFXML()));
            DisplayRecipeViewController displayRecipeViewController = loader.getController();
            displayRecipeViewController.setListener(this);
            displayRecipeViewController.definitionTableViewRecipe();
            displayRecipeViewController.defineIngredientTableView();
            loadRecipeList(displayRecipeViewController);
        } catch (IOException | AccessException e) {
            AlertBoxGenerator.showError("Erreur dans l'affichage des recettes", e);
        }
    }

    /**
     * @param displayRecipeViewController reference to the controller view in order to call
     *                                    some initialize methods
     * load all the recipe for the view
     */
    private void loadRecipeList(DisplayRecipeViewController displayRecipeViewController) throws AccessException {
        Vector<Recipe> vectorRecipes = recipeDAO.selectAll();
        ObservableList<Recipe> observableListRecipe = FXCollections.observableArrayList();
        observableListRecipe.addAll(vectorRecipes);
        displayRecipeViewController.loadAllRecipes(observableListRecipe);
    }

    @Override
    public void createNewRecipe() throws IOException, AccessException {
        getEditionRecipeViewController();
        initializeEditionRecipeViewController();
    }

    @Override
    public void modifyRecipe(Recipe selectedRecipe) throws IOException, AccessException {
        editionRecipeViewController = getEditionRecipeViewController();
        initializeEditionRecipeViewController();
        editionRecipeViewController.initializeSelectedRecipe(selectedRecipe); // set the selected recipe we want to modify
        editionRecipeViewController.setEditMode();
        editionRecipeViewController.setImageRecipe(getImage(selectedRecipe));
    }

    @Override
    public Image getImage(Recipe recipe) {
        return imageController.getImage(recipe);
    }

    private void initializeEditionRecipeViewController() throws AccessException {
        editionRecipeViewController.setListener(this);

        editionRecipeViewController.definitionTableViewProduct();
        editionRecipeViewController.definitionTableViewInstruction();
        editionRecipeViewController.initializeCookingStyle();
    }

    private EditionRecipeViewController getEditionRecipeViewController() throws IOException {
        FXMLLoader loader = getFxmlLoader(stage, EditionRecipeViewController.class.getResource(Windows.CreateRecipe.getPathToFXML()));
        editionRecipeViewController = loader.getController();
        return editionRecipeViewController;
    }

    /**
     * @param recipe the recipe we want to delete
     * this method will delete the recipe from the database
     * Overriden from displayRecipeListener
     */
    @Override
    public void deleteSelectedRecipe(Recipe recipe) {
        try {
            recipeDAO.delete(recipe);
            AlertBoxGenerator.showInfo("La Recette a bien été supprimée", "");
        } catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur dans la suppréssion de la recette", e);
        }
    }

    /**
     * Overriden from all the listener of sub controllers
     */
    @Override
    public void returnToRecipeDisplay() {
        this.show();
    }



    private Vector<IngredientRecipe> buildIngredientsRecipe(Vector<Vector<String>> ingredientsName) throws FilterException, AccessException {
        Vector<Ingredient> ingrFiltered;
        Filter<Ingredient> filter;
        Vector<IngredientRecipe> ingredientsRecipe = new Vector<>();
        boolean invalid = false;
        for (Vector<String> vec : ingredientsName) {
            filter = new Filter<>();
            filter.addField(new IngredientFieldName(vec.get(0)));
            ingrFiltered = ingredientDAO.select(filter);
            if (ingrFiltered.size() == 1) {
                int quant = Integer.parseInt(vec.get(2));
                ingredientsRecipe.add(new IngredientRecipe(ingrFiltered.get(0), new Unit(vec.get(1)), quant));
            } else {invalid = true;}
        }
        if (invalid) {AlertBoxGenerator.showWarning("Certains ingrédients n'existent pas", "Ils ne peuvent pas être ajoutés");}
        return ingredientsRecipe;
    }


    /**
     * @param path path to the json that we selected from the EditionRecipe View
     * Overriden from EditionRecipeListener
     */
    @Override
    public void importJSON(String path) {
        try {
            RecipeJSONReader jsonReader = new RecipeJSONReader(path);
            Vector<Vector<String>> ingredientsName = jsonReader.getIngredientsName();
            Vector<IngredientRecipe> ingredientsRecipe = buildIngredientsRecipe(ingredientsName);
            Recipe recipe = jsonReader.getRecipe(ingredientsRecipe);

            editionRecipeViewController.initializeSelectedRecipe(recipe);
        } catch (Exception e) {
            AlertBoxGenerator.showError("Erreur dans l'import du fichier JSON \n Vérifiez que les noms, types et unités sont valides", e);
        }
    }

    private void savePicture(Recipe recipe) throws IOException, AccessException {
        if (this.file != null) {
            String path= "./img/recipe/" + recipe.getName()+".png";
            Files.copy(file.toPath(),(new File(path)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            recipeDAO.setImage(recipe, path);
        }

    }

    /**
     * @param recipe the recipe that we create
     * Overriden from the EditionRecipeListener
     */
    @Override
    public void addRecipe(Recipe recipe) {
        try {
            recipeDAO.insert(recipe);
            savePicture(recipe);
            AlertBoxGenerator.showInfo("La Recette a bien été ajoutée", "Vous allez être ramené sur la page d'affichage");
            this.show();
        }
        catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur lors de la création", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateRecipe(Recipe recipe) {
        try {
            recipeDAO.update(recipe);
            savePicture(recipe);
            AlertBoxGenerator.showInfo("La Recette a bien été mise à jour", "Vous allez être ramené sur la page d'affichage");
            this.show();
        } catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur lors de la modification", e );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@return Vector of all the possible ingredients names (for the dynamic completion fo ingredients)}
     */
    @Override
    public Vector<String> getPossibleSuggestionsName() throws AccessException {
        Vector<Ingredient> ingredientsDTO = ingredientDAO.selectAll();
        Vector<String> ingredientsDTOName = new Vector<>();
        for (Ingredient ingredient : ingredientsDTO) {
            ingredientsDTOName.add(ingredient.getName());
        }
        return ingredientsDTOName;
    }

    @Override
    public Vector<Ingredient> requestIngredients(String ingredientName) throws AccessException, FilterException {
        Filter<Ingredient> ingredientFilter = new Filter<>();
        ingredientFilter.addField(new IngredientFieldName(ingredientName));
        return ingredientDAO.select(ingredientFilter);
    }

    /**
     * {@return Vector of all the possible types names (for the dynamic completion fo ingredients)}
     */
    @Override
    public Vector<String> getPossibleSuggestionsTypes() throws AccessException {
        Vector<RecipeCookingStyle> recipeCooking = recipeCookingDAO.selectAll();
        Vector<String> recipeTypes = new Vector<>();
        for(RecipeCookingStyle recipeCookingStyle: recipeCooking)
            recipeTypes.add(recipeCookingStyle.style());
        return recipeTypes;
    }

    @Override
    public void uploadImage() {
        final FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            editionRecipeViewController.setImageRecipe(image);
        }
    }
}
