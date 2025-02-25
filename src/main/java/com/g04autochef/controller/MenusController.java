package com.g04autochef.controller;

import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.Picturable;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller coordinating MenusDisplayController and MenusEditionController
 */
public class MenusController <MenuWeeklyDAO extends DAO<MenuWeekly> & Updatable<MenuWeekly>, RecipeDAO extends DAO<Recipe> & Picturable<Recipe>, RecipeTypeDAO extends DAO<RecipeType>, MenuDailyDAO extends DAO<MenuDaily>, ShoppingListDAO extends DAO<ShoppingList>, RecipeCookingStyleDAO extends DAO<RecipeCookingStyle>> extends Controller implements MenusDisplayController.MenusDisplayListener, MenusEditionController.MenusEditionListener {

    private final MenusDisplayController<MenuWeeklyDAO, MenuDailyDAO, ShoppingListDAO, RecipeDAO> menusDisplayController;
    private final MenusEditionController<MenuWeeklyDAO, RecipeDAO, RecipeTypeDAO, MenuDailyDAO, RecipeCookingStyleDAO> menusEditionController;

    public MenusController(Stage stage, ControllerListener mainController, MenuWeeklyDAO weeklyDAO, RecipeDAO recipeDAO, RecipeTypeDAO recipeTypeDAO, MenuDailyDAO dailyDAO, ShoppingListDAO shoppingListDAO, RecipeCookingStyleDAO recipeCookingStyleDAO) {
        super(mainController);
        menusDisplayController = new MenusDisplayController<>(stage, mainController, weeklyDAO, dailyDAO, shoppingListDAO, recipeDAO);
        menusDisplayController.setListener(this);
        menusEditionController = new MenusEditionController<>(stage, mainController, weeklyDAO, recipeDAO, recipeTypeDAO, dailyDAO, recipeCookingStyleDAO);
        menusEditionController.setListener(this);
    }

    protected void show() {
        menusDisplayController.show();
    }

    public void createMenu() {
        menusEditionController.show();
    }


    public void modifyMenu(MenuWeekly menuWeekly) {
        try {
            menusEditionController.modifyMenu(menuWeekly);

        } catch (IOException e) {
            AlertBoxGenerator.showError("Erreur lors du chargement de la page de modification de menu", e);
        }
    }

    @Override
    public void returnToMenuManager() {
        show();
    }

    /**
     * Set the settings in the new stage to display
     * @param newStage stage to display
     * @param s scene to set
     * @param title stage's title
     * @param blockingView boolean for blocking stage or not
     */
    public void setSecondaryStage(Stage newStage, Scene s, String title, Boolean blockingView) {
        newStage.setTitle(title);
        newStage.getIcons().add(new Image(ICON_URL));
        newStage.setScene(s);
        newStage.setResizable(false);
        if (blockingView) {
            newStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
        }
        newStage.showAndWait();
    }
}
