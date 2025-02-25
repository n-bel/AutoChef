package com.g04autochef.controller;

import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.Updatable;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.filterExceptions.FilterException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.recipeFields.RecipeFieldStyle;
import com.g04autochef.data_access.filters.recipeFields.RecipeFieldType;
import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.TimeOfTheDay;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.FXComponents.DayFX;
import com.g04autochef.view.FXComponents.TypeAndQuantityFX;
import com.g04autochef.view.Windows;
import com.g04autochef.view.menu.GenerateMultipleMenusViewController;
import com.g04autochef.view.menu.MenuEditionViewController;
import com.g04autochef.view.menu.MenuManagerViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

/**
 * Controller implementing interfaces to communicate with the MenuEditionViewController
 * and GenerateMultipleMenusViewController
 */
public class MenusEditionController <MenuWeeklyDAO extends DAO<MenuWeekly> & Updatable<MenuWeekly>, RecipeDAO extends DAO<Recipe>, RecipeTypeDAO extends DAO<RecipeType>, MenuDailyDAO extends DAO<MenuDaily>, RecipeCookingStyleDAO extends DAO<RecipeCookingStyle>> extends Controller implements MenuEditionViewController.EditionMenuListener, GenerateMultipleMenusViewController.GenerateMenusListener{

    MenusEditionListener listener;
    private final MenuWeeklyDAO weeklyDAO;
    private final RecipeDAO recipeDAO;
    private final RecipeTypeDAO recipeTypeDAO;
    private final MenuDailyDAO dailyDAO;
    private final RecipeCookingStyleDAO recipeCookingStyleDAO;
    private final Stage stage;
    private final Vector<String> daysNameDisplayed = new Vector<>(Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"));
    private Vector<Vector<String>> menusDays;
    private Vector<Vector<Integer>> numbersDays;
    private Stage multipleGenStage;
    private MenuEditionViewController menuEditionViewController;
    private final List<TimeOfTheDay> vecTimes = Arrays.asList(TimeOfTheDay.values());
    private final List<DayOfTheWeek> vecDays = Arrays.asList(DayOfTheWeek.values());
    private Vector<Recipe> vecRecipe;
    private Vector<RecipeType> vecTypes;
    private Vector<RecipeCookingStyle> vecStyles;
    private HashMap<String , Recipe> recipeNameAndRecipe;
    private HashMap<String, RecipeType> typeAndRecipeType;
    private HashMap<String, RecipeCookingStyle> styleAndRecipeCookingStyle;
    private final int DEFAULT_NUMBER_PEOPLE = 1;
    private final String DEFAULT_DISH = "";


    public MenusEditionController(Stage stage, ControllerListener mainController, MenuWeeklyDAO weeklyDAO, RecipeDAO recipeDAO, RecipeTypeDAO recipeTypeDAO, MenuDailyDAO dailyDAO, RecipeCookingStyleDAO recipeCookingStyleDAO){
        super(mainController);
        this.weeklyDAO = weeklyDAO;
        this.recipeDAO = recipeDAO;
        this.recipeTypeDAO = recipeTypeDAO;
        this.dailyDAO = dailyDAO;
        this.recipeCookingStyleDAO = recipeCookingStyleDAO;
        this.stage = stage;
    }

    public void setListener(MenusEditionListener listener) {
        this.listener = listener;
    }

    /**
     * Generate multiple menus based on the values of the GenerateMultiple view
     * Fill with random dishes
     * @param dayStart Start day for the generation
     * @param dayStop Stop day for the generation
     * @param typesAndQuantities Quantity to generate for every type and style
     */
    @Override
    public void generateMultipleMenus(String dayStart, String dayStop, ObservableList<TypeAndQuantityFX> typesAndQuantities, int nbPeople) {
        int indexStart = daysNameDisplayed.indexOf(dayStart);
        int indexStop = daysNameDisplayed.indexOf(dayStop);
        int maxDishes = (indexStop - indexStart + 1) * 3;
        int counter = 0;
        for (TypeAndQuantityFX t : typesAndQuantities) {
            counter += t.getSpinner().getValue();
        }
        if (indexStart > indexStop) {   // if startDay after stopDay
            AlertBoxGenerator.showWarning("Il y a un problème avec l'ordre des jours", "Veuillez respecter l'ordre des jours de la semaine");
        } else if (counter > maxDishes) {   // if more than 3*days dishes requested
            AlertBoxGenerator.showWarning("Il y a trop des plats choisis", "Veuillez réduire le nombre de plats ou augmenter le nombre de jours concernés");

        } else {
            Vector<String> dishes = new Vector<>();
            for (TypeAndQuantityFX t : typesAndQuantities){
                int i = t.getSpinner().getValue();
                if (i > 0) {
                    for (int j = 0; j < i; j++) {
                        dishes.add(getRandomMenu(t.getType()));
                    }
                }
            }
            for (int i = dishes.size(); i < maxDishes; i++) {
                dishes.add(getRandomMenu(null));
            }
            Random random = new Random();
            int randomIndex;
            for (int i = dishes.size() - 1; i >= 0; i--) {
                randomIndex = random.nextInt(i+1);

                menusDays.get(indexStart + (i/3)).set(i%3, dishes.get(randomIndex));
                numbersDays.get(indexStart + (i/3)).set(i%3, nbPeople);
                dishes.remove(randomIndex);
            }
            menuEditionViewController.refreshView();
            multipleGenStage.hide();
        }
    }

    @Override
    public void returnToMenuManager() {
        listener.returnToMenuManager();
    }

    /**
     * Chose a random recipe from the chosen type
     * @param type type of the recipe to chose
     */
    @Override
    public void generationDish(String type, int dayIndex, int menuIndex) {
        String menu = getRandomMenu(type);
        menusDays.get(dayIndex).set(menuIndex, menu);
        menuEditionViewController.refreshView();
    }

    /**
     * Build a MenuWeekly
     * @param name name of the MenuWeekly
     * @return built MenuWeekly
     */
    private MenuWeekly buildMenuWeekly(String name) {
        HashMap<DayOfTheWeek, MenuDaily> menusDaily = new HashMap<>();
        for(int i=6;i>=0;i--){      // skip empty days
            boolean isEmpty = true;
            for (String s : menusDays.get(i)) {
                if (!(s == null) && !s.isBlank()) {
                    isEmpty = false;
                }
            }
            if (!isEmpty){
                Vector<Integer> numbers = numbersDays.get(i);
                Vector<String> menus = menusDays.get(i);
                HashMap<TimeOfTheDay, Integer> people = new HashMap<>();
                HashMap<TimeOfTheDay, Recipe> dishes = new HashMap<>();
                for (int j = 0; j < 3; j++) {
                    if (! menus.get(j).isBlank()) {
                        dishes.put(vecTimes.get(j), recipeNameAndRecipe.get(menus.get(j)));
                        people.put(vecTimes.get(j), numbers.get(j));
                    }
                }
                MenuDaily menuDaily = new MenuDaily(name + i, dishes, people);
                menusDaily.put(vecDays.get(i), menuDaily);
            }
        }
        return new MenuWeekly(name, menusDaily);
    }

    private String getRandomMenu(String type) {
        String menu = DEFAULT_DISH;
        try {
            Random random = new Random();
            if (type == null) {menu =  vecRecipe.get(random.nextInt(vecRecipe.size())).getName();}
            else {
                Filter<Recipe> f = new Filter<>();
                if (typeAndRecipeType.get(type) == null) {f.addField(new RecipeFieldStyle(type));}
                else if (styleAndRecipeCookingStyle.get(type) == null) {f.addField(new RecipeFieldType(type));}
                Vector<Recipe> recipe = recipeDAO.select(f);
                if (recipe.size() > 0) {
                    int i = random.nextInt(recipe.size());
                    menu = recipe.get(i).getName();
                }
            }
        } catch (AccessException | FilterException e) {
            AlertBoxGenerator.showError("Erreur en générant un menu", e);
        }
        return menu; // Return after catch but program stays coherent, at worse view will display a blank
    }

    /**
     * initialize new values in the MenuEditionView
     */
    private void initializeNewMenuValues() {
        setNewMenuDefaultValues();
        ObservableList<DayFX> days = FXCollections.observableArrayList();
        for (int i = 0; i < daysNameDisplayed.size(); ++i) {
            days.add(new DayFX(daysNameDisplayed.get(i), i));
        }
        menuEditionViewController.initializeTableViewListener();
        menuEditionViewController.initializeNumberOfPeople();
        menuEditionViewController.initializeDays(days);
        menuEditionViewController.initializeMenuLists();
    }
    /**
     * set the default values on the initialization
     */
    private void setNewMenuDefaultValues() {
        menusDays = new Vector<>();
        numbersDays = new Vector<>();
        for (int i = 0; i < vecDays.size(); ++i) {
            menusDays.add(new Vector<>(Arrays.asList(DEFAULT_DISH, DEFAULT_DISH, DEFAULT_DISH)));
            numbersDays.add(new Vector<>(Arrays.asList(DEFAULT_NUMBER_PEOPLE, DEFAULT_NUMBER_PEOPLE, DEFAULT_NUMBER_PEOPLE)));
        }
    }
    /**
     * save the day's values
     * @param menus day's recipes
     * @param number day's number of people
     * @param index index of the day
     */
    public void saveDay(Vector<String> menus, Vector<Integer> number, int index) {
        for (int i = 0; i < menus.size(); i++) {
            menusDays.get(index).set(i, menus.get(i));
            numbersDays.get(index).set(i, number.get(i));
        }
    }

    /**
     * load the new day's values in the MenuEditionView
     * @param newIndex new day's index
     */
    public void loadNewValues(int newIndex) {
        menuEditionViewController.setMenusValues(menusDays.get(newIndex));
        menuEditionViewController.setNumberValues(numbersDays.get(newIndex));
    }

    /**
     * load the MenuWeekly's values when starting the edit mode
     * @param menuWeekly MenuWeekly to load
     */
    private void loadMenuValues(MenuWeekly menuWeekly) {
        for (int i = 0; i < vecDays.size(); ++i) {
            MenuDaily daily = menuWeekly.getMenuDay(vecDays.get(i));
            if (daily != null) {
                for (int j = 0; j < vecTimes.size(); ++j) {
                    Recipe recipe = daily.getDaytimeRecipe(vecTimes.get(j));
                    if (recipe != null) {
                        menusDays.get(i).set(j,recipe.getName());
                        numbersDays.get(i).set(j, daily.getDaytimePeople(vecTimes.get(j)));
                    }
                }
            }
        }
    }
    /**
     * reset a dish in the day
     * @param dayIndex index of the day
     * @param menuIndex index of the dish to reset
     */
    public void resetValue(int dayIndex, int menuIndex) {
        menusDays.get(dayIndex).set(menuIndex, DEFAULT_DISH);
        numbersDays.get(dayIndex).set(menuIndex, DEFAULT_NUMBER_PEOPLE);
    }

    /**
     * reset all the MenuWeekly's values
     */
    public void resetAll() {
        setNewMenuDefaultValues();
        menuEditionViewController.refreshView();
    }

    @Override
    protected void show() {
        try {
            vecRecipe = recipeDAO.selectAll();
            vecTypes = recipeTypeDAO.selectAll();
            vecStyles = recipeCookingStyleDAO.selectAll();
        } catch (AccessException e) {
            // Program state is coherent even if select fails, selection will simply be empty
            AlertBoxGenerator.showError("Erreur en affichant une recette", e);
        }

        recipeNameAndRecipe = new HashMap<>();
        typeAndRecipeType = new HashMap<>();
        styleAndRecipeCookingStyle = new HashMap<>();

        ObservableList<String> typesAndStylesInDB = FXCollections.observableArrayList();
        ObservableList<String> dishesInDB = FXCollections.observableArrayList();

        for (Recipe r : vecRecipe) {recipeNameAndRecipe.put(r.getName(), r);}
        for (RecipeType r : vecTypes) {typeAndRecipeType.put(r.getType(), r);}
        for (RecipeCookingStyle r : vecStyles) {styleAndRecipeCookingStyle.put(r.style(), r);}

        for (Recipe recipe : vecRecipe) {dishesInDB.add(recipe.getName());}
        for (RecipeType type : vecTypes) {typesAndStylesInDB.add(type.getType());}
        for (RecipeCookingStyle style : vecStyles) {typesAndStylesInDB.add(style.style());}

        try {
            FXMLLoader loader = getFxmlLoader(stage, MenuEditionViewController.class.getResource(Windows.AddMenu.getPathToFXML()));
            menuEditionViewController = loader.getController();
            menuEditionViewController.setListener(this);
            menuEditionViewController.setDishes(dishesInDB);
            menuEditionViewController.setTypes(typesAndStylesInDB);
        } catch (IOException e){
            AlertBoxGenerator.showError("Erreur lors du chargement de l'édition des menus", e);
        }
        initializeNewMenuValues();
    }
    /**
     * Build and inesert the MenuWeekly in the storage
     * @param name name of the MenuWeekly to create
     */
    @Override
    public void saveMenu(String name) {
        try {
            MenuWeekly menuWeekly = buildMenuWeekly(name);
            for (Map.Entry<DayOfTheWeek, MenuDaily> menuDaily : menuWeekly.getAllMenuDaily().entrySet()) {
                dailyDAO.insert(menuDaily.getValue());
            }
            weeklyDAO.insert(menuWeekly);
            AlertBoxGenerator.showInfo("Le menu a bien été créé", "Vous allez être ramené sur la vue globale des menus");
            returnToMenuManager();
        } catch (IllegalArgumentException e) {
            AlertBoxGenerator.showError("Un élement est vide et ne peut pas l'être", e);
        }
        catch (AccessException e) {
            AlertBoxGenerator.showError("Le nom de menu rentré n'est pas valide", e);
        }
    }

    /**
     * load the MenuEdition view and set it to editMode
     * @param menuWeekly MenuWeekly to edit
     */
    public void modifyMenu(MenuWeekly menuWeekly) throws IOException {
        show();
        menuEditionViewController.setEditMode(menuWeekly.getName());
        loadMenuValues(menuWeekly);
        menuEditionViewController.refreshView();
    }

    /**
     * Update the MenuWeekly in the storage
     * @param name name of the MenuWeekly to update
     */
    @Override
    public void updateMenu(String name) {
        try {
            MenuWeekly menuWeekly = buildMenuWeekly(name);
            weeklyDAO.update(menuWeekly);
            AlertBoxGenerator.showInfo("Le menu a bien été mis à jour", "Vous allez être ramené sur la vue globale des menus");
            returnToMenuManager();
        } catch (IllegalArgumentException e) {
            AlertBoxGenerator.showError("Un élement est vide et ne peut pas l'être", e);
        }
        catch (AccessException e) {
            AlertBoxGenerator.showError("Erreur dans la mise à jour du menu", e);
        }
    }

    /**
     * Load the GenerateMultiple view
     */
    public void generateMultiple() {
        multipleGenStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MenuManagerViewController.class.getResource(Windows.MultipleMenus.getPathToFXML()));
        try {
            final int STAGE_GEN_WIDTH = 428;
            final int STAGE_GEN_HEIGHT = 549;
            Scene s = new Scene(fxmlLoader.load(), STAGE_GEN_WIDTH, STAGE_GEN_HEIGHT);
            GenerateMultipleMenusViewController genMenusInput = fxmlLoader.getController();
            genMenusInput.setListener(this);
            genMenusInput.setDays(daysNameDisplayed);
            Vector<String> types = new Vector<>();
            for (RecipeType r : vecTypes) {types.add(r.getType());}
            for (RecipeCookingStyle r : vecStyles) {types.add(r.style());}
            genMenusInput.setTypes(types);
            genMenusInput.initializeTableView();
            final String MULTIPLE_GEN_TITLE = "Génération multiple de menus";
            listener.setSecondaryStage(multipleGenStage, s, MULTIPLE_GEN_TITLE, true);
        } catch (IOException e) {
            AlertBoxGenerator.showError("Erreur dans la génération de la fenêtre", e);
        }
    }

    public interface MenusEditionListener {
        void returnToMenuManager();
        void setSecondaryStage(Stage newStage, Scene s, String title, Boolean blockingView);
    }
}
