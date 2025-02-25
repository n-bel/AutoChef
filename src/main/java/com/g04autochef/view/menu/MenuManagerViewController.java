package com.g04autochef.view.menu;

import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.TimeOfTheDay;
import com.g04autochef.model.storableDAO.*;
import com.g04autochef.view.tableConstructors.TableViewable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.*;

/**
 * View Controller that let display every MenuWeekly
 * Communicate with the MenuMangerController which implements the MenuManagerViewListener interface
 */
public class MenuManagerViewController implements Initializable, TableViewable<TableViewable.TableViewListener> {

    private MenuManagerViewListener menuManagerController;
    @FXML private Label morningNb0, noonNb0,  eveningNb0;
    @FXML private Label morningNb1, noonNb1, eveningNb1;
    @FXML private Label morningNb2, noonNb2, eveningNb2;
    @FXML private Label morningNb3, noonNb3, eveningNb3;
    @FXML private Label morningNb4,  noonNb4, eveningNb4;
    @FXML private Label morningNb5, noonNb5, eveningNb5;
    @FXML private Label morningNb6, noonNb6, eveningNb6;
    @FXML private Button morningButton0, noonButton0, eveningButton0;
    @FXML private Button morningButton1, noonButton1, eveningButton1;
    @FXML private Button morningButton2, noonButton2, eveningButton2;
    @FXML private Button morningButton3, noonButton3, eveningButton3;
    @FXML private Button morningButton4, noonButton4, eveningButton4;
    @FXML private Button morningButton5, noonButton5, eveningButton5;
    @FXML private Button morningButton6, noonButton6, eveningButton6;
    @FXML private TableView<MenuWeekly> listOfWeeks;
    @FXML private TableColumn<MenuWeekly, String> colWeek;
    @FXML private Button deleteMenuButton, modifyButton;
    @FXML private Button exportMenuButton;
    private Vector<Vector<Label>> vecLabels;
    private Vector<Vector<Button>> vecButtons;
    private Vector<MenuWeekly> menusWeekly;
    private Vector<TimeOfTheDay> timesOfTheDay;
    private Vector<DayOfTheWeek> daysOfTheWeek;

    /**
     * Set the listener to communicate with MenusDisplayController
     * @param menuManagerController MenusDisplayController
     */
    public void setListener(MenuManagerViewListener menuManagerController) {
        this.menuManagerController = menuManagerController;
    }

    /**
     * Initialize values
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeVecLabels();
        initializeButtons();
        initializeListOfWeeksListener();
        initializeDaysOfTheWeek();
        initializeTimesOfTheDay();
    }

    private void initializeTimesOfTheDay(){
        timesOfTheDay = new Vector<>(List.of(TimeOfTheDay.values()));
    }

    private void initializeDaysOfTheWeek(){
        daysOfTheWeek = new Vector<>(List.of(DayOfTheWeek.values()));
    }

    private void initializeListOfWeeksListener() {
        listOfWeeks.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadLabels(newValue);
            }
        });
    }

    private void initializeButtons() {
        initializeVecButtons();
        for (int i = 0; i < vecButtons.size(); ++i) {
            final int dayIndex = i;
            for (int j = 0; j < vecButtons.get(i).size(); ++j) {
                final int timeIndex = j;
                vecButtons.get(i).get(j).setOnAction(a -> menuManagerController.menuRecipeClicked(listOfWeeks.getSelectionModel().getSelectedItem(), dayIndex,timeIndex));
            }
        }
    }

    private void initializeVecButtons() {
        vecButtons = new Vector<>(Arrays.asList(
                new Vector<>(Arrays.asList(morningButton0, noonButton0, eveningButton0)),
                new Vector<>(Arrays.asList(morningButton1, noonButton1, eveningButton1)),
                new Vector<>(Arrays.asList(morningButton2, noonButton2, eveningButton2)),
                new Vector<>(Arrays.asList(morningButton3, noonButton3, eveningButton3)),
                new Vector<>(Arrays.asList(morningButton4, noonButton4, eveningButton4)),
                new Vector<>(Arrays.asList(morningButton5, noonButton5, eveningButton5)),
                new Vector<>(Arrays.asList(morningButton6, noonButton6, eveningButton6))
        ));
    }

    private void initializeVecLabels() {
        vecLabels = new Vector<>(Arrays.asList(
                new Vector<>(Arrays.asList(morningNb0, noonNb0, eveningNb0)),
                new Vector<>(Arrays.asList(morningNb1, noonNb1, eveningNb1)),
                new Vector<>(Arrays.asList(morningNb2, noonNb2, eveningNb2)),
                new Vector<>(Arrays.asList(morningNb3, noonNb3, eveningNb3)),
                new Vector<>(Arrays.asList(morningNb4, noonNb4, eveningNb4)),
                new Vector<>(Arrays.asList(morningNb5, noonNb5, eveningNb5)),
                new Vector<>(Arrays.asList(morningNb6, noonNb6, eveningNb6))
        ));
    }

    /**
     * Set the values of the MenuWeekly on the labels
     * @param newValue the MenuWeekly selected
     */
    private void loadLabels(MenuWeekly newValue) {
        HashMap<DayOfTheWeek, MenuDaily> allMenuDaily = newValue.getAllMenuDaily();
        final int MAX_DAYS_IN_WEEK = daysOfTheWeek.size();
        final int MAX_MENUS_PER_DAY = timesOfTheDay.size();
        for (int dayNumber = 0; dayNumber < MAX_DAYS_IN_WEEK; dayNumber++) {
            final MenuDaily menuDay = allMenuDaily.get(daysOfTheWeek.get(dayNumber));
            for (int menuTimeOfDay = 0; menuTimeOfDay < MAX_MENUS_PER_DAY; menuTimeOfDay++) {
                resetLabelValuesAtGivenPosition(dayNumber, menuTimeOfDay);
                setLabelForMenu(dayNumber, menuDay, menuTimeOfDay);
            }
        }

    }

    private void setLabelForMenu(int dayNumber, MenuDaily menuDay, int menuTimeOfDay) {
        if (menuDay != null) {
            final Recipe recipe = menuDay.getDaytimeRecipe(timesOfTheDay.get(menuTimeOfDay));
            if (recipe != null) {
                final String recipeName = recipe.getName();
                vecButtons.get(dayNumber).get(menuTimeOfDay).setText(recipeName);
                vecButtons.get(dayNumber).get(menuTimeOfDay).setDisable(false);
                if (!recipeName.isBlank()) {
                    int nb = menuDay.getDaytimePeople(timesOfTheDay.get(menuTimeOfDay));
                    String nbText = " personne";
                    if (nb > 1) {
                        nbText += "s";
                    }
                    vecLabels.get(dayNumber).get(menuTimeOfDay).setText(nb + nbText);
                }
            }
        }
    }

    private void resetLabelValuesAtGivenPosition(int i, int j) {
        final String EMPTY = "";
        vecButtons.get(i).get(j).setText(EMPTY);
        vecLabels.get(i).get(j).setText(EMPTY);
        vecButtons.get(i).get(j).setDisable(true);
    }

    /**
     * Set the MenuWeekly vector
     * @param vecMenuWeekly vector of MenuWeekly
     */
    public void setMenuWeekly(Vector<MenuWeekly> vecMenuWeekly) {
        this.menusWeekly = vecMenuWeekly;
    }

    /**
     * Initialize the ListView
     */
    public void initializeListView() {
        final Vector<String> attributeList = new Vector<>();
        attributeList.add("name");

        final Vector<TableColumn<?, ?>> columnList = new Vector<>();
        columnList.add(colWeek);

        ObservableList<MenuWeekly> vecObs = FXCollections.observableArrayList();
        vecObs.addAll(menusWeekly);

        constructionTableView(listOfWeeks, attributeList, columnList);
        listOfWeeks.setItems(vecObs);
        listOfWeeks.getSelectionModel().selectFirst();
        if (listOfWeeks.getItems().size() > 0) {
            deleteMenuButton.setDisable(false);
            modifyButton.setDisable(false);
            exportMenuButton.setDisable(false);
        }
    }

    /**
     * Ask the controller to switch to the menu creator view
     */
    @FXML
    private void newMenu() {
        menuManagerController.newMenu();
    }

    /**
     * Tell the Controller to delete the MenuWeekly and delete if from the View
     */
    @FXML
    private void deleteMenu(){
        menuManagerController.deleteMenu(listOfWeeks.getSelectionModel().getSelectedItem());
        listOfWeeks.getItems().remove(listOfWeeks.getSelectionModel().getSelectedItem());
        disableButtonsIfNoMenuWeeklyPresent();
    }

    private void disableButtonsIfNoMenuWeeklyPresent() {
        if (listOfWeeks.getItems().isEmpty()) {
            deleteMenuButton.setDisable(true);
            modifyButton.setDisable(true);
            exportMenuButton.setDisable(true);
        }
    }

    /**
     * Tell the Controller to set the EditionView for the MenuWeekly
     */
    @FXML
    private void modifyMenu() {
        menuManagerController.modifyMenu(listOfWeeks.getSelectionModel().getSelectedItem());
    }

    /**
     * Ask the Controller to go back to main menu
     */
    @SuppressWarnings("unused")
    @FXML private void returnToMainMenu() {
        menuManagerController.returnToMainMenu();
    }

    @FXML private void exportMenuToShoppingList() {
       menuManagerController.exportMenuToShoppingList(listOfWeeks.getSelectionModel().getSelectedItem());
    }
    /**
     * The listener of MenuManagerViewController in order to communicate with the MenusDisplayController
     */
    public interface MenuManagerViewListener{
        void newMenu();
        void returnToMainMenu();
        void deleteMenu(MenuWeekly menuWeekly);
        void modifyMenu(MenuWeekly menuWeekly);
        void menuRecipeClicked(MenuWeekly weekly, int i, int j);
        void exportMenuToShoppingList(MenuWeekly menuWeekly);
    }
}