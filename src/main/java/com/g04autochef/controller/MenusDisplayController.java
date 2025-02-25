package com.g04autochef.controller;

import com.g04autochef.controller.utils.ImageController;
import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.Picturable;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.MenuToShoppingListConverter;
import com.g04autochef.model.TimeOfTheDay;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.Windows;
import com.g04autochef.view.menu.MenuManagerViewController;
import com.g04autochef.view.recipe.DisplayRecipeViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Controller implementing interfaces to communicate with the MenuManagerViewController
 */
public class MenusDisplayController<MenuWeeklyDAO extends DAO<MenuWeekly> & Updatable<MenuWeekly>,MenuDailyDAO extends DAO<MenuDaily>, ShoppingListDAO extends DAO<ShoppingList>, RecipeDAO extends DAO<Recipe> & Picturable<Recipe>> extends Controller implements MenuManagerViewController.MenuManagerViewListener {

    private final Stage stage;
    private final MenuWeeklyDAO weeklyDAO;
    private final MenuDailyDAO dailyDAO;
    private final ShoppingListDAO shoppingListDAO;
    private final RecipeDAO recipeDAO;
    private final ImageController<Recipe, RecipeDAO> imageController;

    private final List<TimeOfTheDay> vecTimes = Arrays.asList(TimeOfTheDay.values());
    private final List<DayOfTheWeek> vecDays = Arrays.asList(DayOfTheWeek.values());
    private MenusDisplayListener listener;

    /**
     * @param stage Window will contain the scene
     * @param mainController Reference to the MainController
     */
    public MenusDisplayController(Stage stage, ControllerListener mainController, MenuWeeklyDAO weeklyDAO, MenuDailyDAO dailyDAO, ShoppingListDAO shoppingListDAO, RecipeDAO recipeDAO){
        super(mainController);
        this.weeklyDAO = weeklyDAO;
        this.dailyDAO = dailyDAO;
        this.shoppingListDAO = shoppingListDAO;
        this.recipeDAO = recipeDAO;
        imageController = new ImageController<>(recipeDAO);
        this.stage = stage;
    }

    public void setListener(MenusDisplayListener listener) {
        this.listener = listener;
    }
    /**
     * Method overriden from Controller
     * will load the View and show it
     */
    @Override
    public void show() {
        try {
            FXMLLoader loader = getFxmlLoader(stage, MenuManagerViewController.class.getResource(Windows.MenuManager.getPathToFXML()));
            MenuManagerViewController menuManagerViewController = loader.getController();
            menuManagerViewController.setListener(this);
            Vector<MenuWeekly> vecMenuWeekly = weeklyDAO.selectAll();

            menuManagerViewController.setMenuWeekly(vecMenuWeekly);
            menuManagerViewController.initializeListView();
        } catch (IOException | AccessException e) {
            AlertBoxGenerator.showError("Erreur en affichant le manager de menus", e);
        }
    }

    /**
     * Change the view to the Menu creator
     */
    @Override
    public void newMenu() {
        listener.createMenu();
    }

    /**
     * try to delete the MenuWeekly from the storage
     * @param menuWeekly MenuWeekly to delete
     */
    @Override
    public void deleteMenu(MenuWeekly menuWeekly) {
        try {
            weeklyDAO.delete(menuWeekly);
            for (Map.Entry<DayOfTheWeek, MenuDaily> menuDaily : menuWeekly.getAllMenuDaily().entrySet()) {
                dailyDAO.delete(menuDaily.getValue());
            }
            this.show();
        } catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur lors de la suppréssion d'un menu", e);
        }
    }


    /**
     * load the MenuEdition view and set it to editMode
     * @param menuWeekly MenuWeekly to edit
     */
    @Override
    public void modifyMenu(MenuWeekly menuWeekly) {
    listener.modifyMenu(menuWeekly);
    }

    /**
     * Display a new windows when a recipe is clicked on the MenuManager view
     * @param weekly MenuWeekly chosen
     * @param indexDay index of the day chosen
     * @param indexTime index of the time chosen
     */
    @Override
    public void menuRecipeClicked(MenuWeekly weekly, int indexDay, int indexTime) {
        MenuDaily daily = weekly.getMenuDay(vecDays.get(indexDay));
        final boolean dayExists = daily != null;
        if (dayExists) {
            Recipe recipe = daily.getDaytimeRecipe(vecTimes.get(indexTime));
            final boolean recipeExists = recipe != null;
            if (recipeExists) {
                recipe.ponderateQuantity(daily.getDaytimePeople(vecTimes.get(indexTime)));
                Stage newStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(DisplayRecipeViewController.class.getResource(Windows.DisplayRecipeOnly.getPathToFXML()));
                try {
                    final int STAGE_RECIPE_WIDTH = 1000;
                    final int STAGE_RECIPE_HEIGHT = 570;
                    Scene s = new Scene(fxmlLoader.load(), STAGE_RECIPE_WIDTH, STAGE_RECIPE_HEIGHT);
                    DisplayRecipeViewController displayRecipeViewController = fxmlLoader.getController();
                    displayRecipeViewController.defineIngredientTableView();
                    displayRecipeViewController.displayRecipe(recipe);
                    displayRecipeViewController.setImage(imageController.getImage(recipe));
                    listener.setSecondaryStage(newStage, s, recipe.getName(), false);
                } catch (IOException e) {
                    AlertBoxGenerator.showError("Erreur dans la génération de la fenêtre", e);
                }
            }
        }
    }

    @Override
    public void exportMenuToShoppingList(MenuWeekly menuWeekly) {
        ShoppingList shoppingList = MenuToShoppingListConverter.MenuToShoppingList(menuWeekly);
        try {
            shoppingListDAO.insert(shoppingList);
            AlertBoxGenerator.showInfo("La liste a bien été ajoutée à Mes Listes de Courses!", "");
        } catch (AccessException e) {
            AlertBoxGenerator.showWarning("Une liste de course du même nom existe déjà !", "La liste n'a pas pu être créée !");
        }

    }

    public interface MenusDisplayListener {
        void modifyMenu(MenuWeekly menuWeekly);
        void createMenu();
        void setSecondaryStage(Stage newStage, Scene s, String title, Boolean blockingView);
    }
}