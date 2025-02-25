package com.g04autochef.view.menu;

import com.g04autochef.view.IntegerSpinner;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.tableConstructors.TableViewable;
import com.g04autochef.view.FXComponents.DayFX;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * View Controller that let the user create or edit a MenuWeekly
 * Communicate with the MenuMangerController which implements the EditionMenuListener interface
 */
public class MenuEditionViewController extends ViewController implements TableViewable<TableViewable.TableViewListener> {


    private ObservableList<String> dishesLists;
    private ObservableList<String> typesLists;
    private EditionMenuListener menuManager;
    private Vector<IntegerSpinner> numberOfPeople;
    private Vector<SpinnerValueFactory<Integer>> valueFactories;
    private Vector<ChoiceBox<String>> types;
    private Vector<TextField> dishes;

    private final int NB_MEAL_PER_DAY = 3;

    @FXML private Button createMenuButton;
    @FXML private TextField menuName;
    @FXML private IntegerSpinner numberOfPeopleMorningSpinner, numberOfPeopleNoonSpinner, numberOfPeopleEveningSpinner;
    @FXML private TableColumn<DayFX, String> colDay;
    @FXML private javafx.scene.control.TableView<DayFX> listOfDays;
    @FXML private TextField dishMorningTextField, dishNoonTextField, dishEveningTextField;
    @FXML private ChoiceBox<String> typeMorning, typeNoon, typeEvening;


    /**
     * Set the listener to communicate with the MenusDisplayController
     * @param menuManager MenusDisplayController
     */
    public void setListener(EditionMenuListener menuManager){
        this.menuManager = menuManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        initializeVectors();
        initListenerDishesTextFields();
    }

    /**
     * Initialize vectors of choiceboxes and spinners
     */
    private void initializeVectors() {
        numberOfPeople = new Vector<>(Arrays.asList(numberOfPeopleMorningSpinner, numberOfPeopleNoonSpinner, numberOfPeopleEveningSpinner));
        dishes = new Vector<>(Arrays.asList(dishMorningTextField, dishNoonTextField, dishEveningTextField));
        types = new Vector<>(Arrays.asList(typeMorning, typeNoon, typeEvening));
    }

    /**
     * Initialize a listener for an interactive name textfield
     */
    private void initListenerDishesTextFields(){
        for (TextField textField : dishes) {
            textField.textProperty().addListener((obs, oldText, newText) -> {
                if (newText != null && oldText != null && !oldText.equals(newText)) {
                    updateDishStyle(textField);
                }
            });
        }
    }

    private boolean dishIsValid(TextField textField) {
        if (textField.getText() != null)
            return dishesLists.contains(textField.getText()) || textField.getText().isBlank() ;
        return false;
    }

    /**
     * Highlight in red the textfield if the style is not correct
     */
    private void updateDishStyle(TextField textField) {
        if (dishIsValid(textField)) {
            textField.setStyle("-fx-border-color: LIGHTSTEELBLUE;");
        }
        else {
            textField.setStyle("-fx-border-color: RED;");
        }
    }

    /**
     * Initialize values of spinners
     */
    public void initializeNumberOfPeople() {
        valueFactories = new Vector<>();
        int MAX_NB_PEOPLE = 100;
        int MIN_NB_PEOPLE = 1;
        for (int i = 0; i<NB_MEAL_PER_DAY; i++){
            valueFactories.add(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NB_PEOPLE, MAX_NB_PEOPLE)); //borne;
        }
        int DEFAULT_NB_PEOPLE = 1;
        setNumberValues(new Vector<>(Arrays.asList(DEFAULT_NB_PEOPLE, DEFAULT_NB_PEOPLE, DEFAULT_NB_PEOPLE)));
    }

    /**
     * Set the values when the day is changed
     * @param menusDay day's dishes name
     */
    public void setMenusValues(Vector<String> menusDay) {
        for (int i=0; i<NB_MEAL_PER_DAY; i++){
            types.get(i).getItems().setAll(typesLists);
            TextFields.bindAutoCompletion(dishes.get(i), dishesLists);
            dishes.get(i).setText(menusDay.get(i));
        }
    }

    /**
     * Set the spinners values
     * @param vec vector of values to set
     */
    public void setNumberValues(Vector<Integer> vec) {
        for (int i=0; i<NB_MEAL_PER_DAY; i++) {
            valueFactories.get(i).setValue(vec.get(i));
            numberOfPeople.get(i).setValueFactory(valueFactories.get(i));
        }
    }

    /**
     * Initialize the TableView
     */
    public void initializeMenuLists(){
        Vector<String> attributeList = new Vector<>();
        Vector<TableColumn<?, ?>> columnList = new Vector<>();
        attributeList.add("dayName");
        columnList.add(colDay);
        constructionTableView(listOfDays, attributeList, columnList);
    }

    /**
     * Save the day's values in the Controller
     */
    private void saveDay(DayFX day) {
        Vector<String> menusDay = getMenuDayNames();
        Vector<Integer> numbersDay = getMenuDayNumbers();
        menuManager.saveDay(menusDay, numbersDay, day.getDayIndex());
    }

    private Vector<Integer> getMenuDayNumbers() {
        Vector<Integer> numbersDay = new Vector<>();
        for (IntegerSpinner numberOfPeopleSpinner : numberOfPeople) {
            numbersDay.add(numberOfPeopleSpinner.getValue());
        }
        return numbersDay;
    }

    private Vector<String> getMenuDayNames() {
        Vector<String> menusDay = new Vector<>();
        for (TextField dishTextField : dishes) {
            if (! dishIsValid(dishTextField))
                dishTextField.clear();
            else
                menusDay.add(dishTextField.getText());
        }
        return menusDay;
    }

    /**
     * Switch the displayed day and save the old one
     * @param oldValue old displayed Day
     * @param newValue new Day to display
     */
    private void changeDay(DayFX oldValue, DayFX newValue) {
        saveDay(oldValue);
        menuManager.loadNewValues(newValue.getDayIndex());
    }

    /**
     * Create the TableView listener
     */
    public void initializeTableViewListener(){
        listOfDays.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            final boolean bothValuesAreNotNull = newValue != null && oldValue != null;
            final boolean onlyNewValueIsNull = newValue != null;

            if (bothValuesAreNotNull) {
                changeDay(oldValue, newValue);
            }
            else if (onlyNewValueIsNull) {
                changeDay(newValue, newValue);
            }
        });
    }

    /**
     * Initialize the TableView
     * @param days list of Days to display
     */
    public void initializeDays(ObservableList<DayFX> days) {
        listOfDays.setItems(days);
        listOfDays.getSelectionModel().selectFirst();
    }

    /**
     * Reload values from the Controller
     */
    public void refreshView() {
        menuManager.loadNewValues(listOfDays.getSelectionModel().getSelectedItem().getDayIndex());
    }

    /**
     * Reset the morning values
     */
    @FXML
    private void resetMorning(){reset(0);}

    /**
     * Reset the noon values
     */
    @FXML
    private void resetNoon(){reset(1);}

    /**
     * Reset the evening values
     */
    @FXML
    private void resetEvening(){reset(2);}

    /**
     * Reset the values
     * @param i place in the vectors (morning/noon/evening)
     */
    private void reset(int i){
        saveDay(listOfDays.getSelectionModel().getSelectedItem());
        menuManager.resetValue(listOfDays.getSelectionModel().getSelectedItem().getDayIndex(), i);
        refreshView();
    }

    /**
     * Chose a random recipe for the morning
     */
    @FXML
    private void generateMorning(){generate(0);}

    /**
     * Chose a random recipe for the noon
     */
    @FXML
    private void generateNoon(){generate(1);}

    /**
     * Chose a random recipe for the evening
     */
    @FXML
    private void generateEvening(){generate(2);}

    /**
     * Chose a random recipe
     * @param i place in the vectors (morning/noon/evening)
     */
    private void generate(int i){
        menuManager.generationDish(types.get(i).getValue(), listOfDays.getSelectionModel().getSelectedItem().getDayIndex(), i);
    }

    /**
     * Tell the Controller to load the GenerateMultipleView
     */
    @FXML
    private void generateMultiple() {
        saveDay(listOfDays.getSelectionModel().getSelectedItem());
        menuManager.generateMultiple();
    }

    /**
     * Tell the controller to save
     */
    @FXML
    private void saveMenu() {
        saveLastSelectedDay();
        menuManager.saveMenu(menuName.getText());
    }

    /**
     * Return to the menu view
     */
    @FXML
    private void returnToMenuManager() {
        menuManager.returnToMenuManager();
    }

    /**
     * Set the dishes name vector
     * @param vec dishes name vector
     */
    public void setDishes(final ObservableList<String> vec) {
        this.dishesLists = vec;
    }

    /**
     * Set the types name vector
     * @param vec types name vector
     */
    public void setTypes(final ObservableList<String> vec) {
        this.typesLists = vec;
    }

    /**
     * Reset all the values
     */
    @FXML
    private void resetMultiple() {
        menuManager.resetAll();
    }

    /**
     * Disable the name TextField and change the onAction Button's method
     * @param name name of the MenuWeekly to edit
     */
    public void setEditMode(String name) {
        menuName.setText(name);
        menuName.setEditable(false);
        createMenuButton.setOnAction(a -> updateMenu());
    }

    /**
     * Tell the Controller to update the MenuWeekly
     */
    @FXML
    private void updateMenu() {
        saveLastSelectedDay();
        menuManager.updateMenu(menuName.getText());
    }

    private void saveLastSelectedDay() {
        final DayFX lastSelectedDay = listOfDays.getSelectionModel().getSelectedItem();
        saveDay(lastSelectedDay);
    }


    /**
     * The listener of MenuEditionViewController in order to communicate with the MenusDisplayController
     */
    public interface EditionMenuListener {
        void returnToMenuManager();
        void generationDish(String type, int dayIndex, int menuIndex);
        void saveMenu(String name);
        void updateMenu(String name);
        void generateMultiple();
        void saveDay(Vector<String> menus, Vector<Integer> number, int index);
        void loadNewValues(int newIndex);
        void resetValue(int dayIndex, int menuIndex);
        void resetAll();
    }
}

