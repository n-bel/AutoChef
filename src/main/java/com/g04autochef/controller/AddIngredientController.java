package com.g04autochef.controller;

import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.model.storableDAO.Ingredient;
import com.g04autochef.model.storableDAO.IngredientType;
import com.g04autochef.model.storableDAO.Unit;
import com.g04autochef.view.ingredient.AddIngredientViewController;
import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.Windows;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Vector;

/**
 * Controller implementing interface to communicate with the AddIngredientViewController
 */
public class AddIngredientController<UnitDAO extends DAO<Unit>, IngredientTypeDAO extends DAO<IngredientType>, IngredientDAO extends DAO<Ingredient>> extends Controller implements AddIngredientViewController.AddIngredientViewListener {

    private final Stage stage;
    private AddIngredientViewController addIngredientViewController;
    private final UnitDAO unitDAO;
    private final IngredientTypeDAO ingredientTypeDAO;
    private final IngredientDAO ingredientDAO;


    public AddIngredientController(Stage stage, ControllerListener mainController, UnitDAO unitDAO, IngredientTypeDAO ingredientTypeDAO, IngredientDAO ingredientDAO){
        super(mainController);
        this.unitDAO = unitDAO;
        this.ingredientTypeDAO = ingredientTypeDAO;
        this.ingredientDAO = ingredientDAO;
        this.stage = stage;
    }

    /**
     * Method overridden from Controller
     * will load the View and show it
     */
    @Override
    public void show() {
        try {
            addIngredientViewController = new AddIngredientViewController();
            FXMLLoader loader = getFxmlLoader(stage, AddIngredientViewController.class.getResource(Windows.AddProduct.getPathToFXML()));
            addIngredientViewController = loader.getController();
            addIngredientViewController.setListener(this);

            Vector<Unit> units = unitDAO.selectAll();
            Vector<IngredientType> types = ingredientTypeDAO.selectAll();
            addIngredientViewController.setBoxesValues(units);
            addIngredientViewController.setTypes(types);
        } catch (IOException | AccessException e) {
            AlertBoxGenerator.showError("Erreur en affichant l'ajout d'ingredient", e);
        }
    }

    /**
     * Build and add the Ingredient to the storage
     * @param name name of the Ingredient to add
     * @param type type of the Ingredient to add
     * @param vecUnites vector of accepted units
     */
    public void addIngredient(String name, String type, Vector<String> vecUnites) {
        try {
            Vector<Unit> unitsToSave = new Vector<>();
            for (String s : vecUnites) {
                unitsToSave.add(new Unit(s));
            }
            Ingredient ingrToAdd = new Ingredient(name, new IngredientType(type), unitsToSave);
            ingredientDAO.insert(ingrToAdd);
            AlertBoxGenerator.showInfo("L'ingrédient a bien été créé", "Vous allez être ramené sur cette page");
            addIngredientViewController.reset();
        } catch (AccessException e) {
            AlertBoxGenerator.showError("Le nom rentré n'est pas valide", e);
        }
    }

    /**
     * Enable or disable the add button on the view
     * depending on the current values
     * @param name name given by the user
     * @param checkBoxes vector of Checkboxes
     */
    public void valueChanged(String name, Vector<CheckBox> checkBoxes) {
        boolean unitSelected = false;
        for (CheckBox box : checkBoxes) {
            if (box.isSelected()) {
                unitSelected = true;
            }
        }
        addIngredientViewController.enableAddButton(unitSelected && !name.isBlank());
    }
}