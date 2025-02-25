package com.g04autochef.view.menu;

import com.g04autochef.view.IntegerSpinner;
import com.g04autochef.view.ViewController;
import com.g04autochef.view.FXComponents.TypeAndQuantityFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * View Controller that let the user chose how many recipes of every types he wants for the week
 * Communicate with the MenuMangerController which implements the GenerateMenusListener interface
 */
public class GenerateMultipleMenusViewController extends ViewController {

    @FXML private TableView<TypeAndQuantityFX> tableView;
    @FXML private TableColumn<TypeAndQuantityFX, String> typesColumn;
    @FXML private TableColumn<TypeAndQuantityFX, IntegerSpinner> quantityColumn;
    @FXML private ComboBox<String> dayStart;
    @FXML private ComboBox<String> dayStop;
    @FXML private IntegerSpinner nbPersonSpinner;
    private ObservableList<TypeAndQuantityFX> types;
    private GenerateMenusListener listener;

    /**
     * Set the controller as listener
     * @param listener controller implementing the GenerateMenuListener interface
     */
    public void setListener(GenerateMenusListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final int MIN_NB_PEOPLE = 1;
        final int MAX_NB_PEOPLE = 100;
        nbPersonSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NB_PEOPLE, MAX_NB_PEOPLE));
    }

    /**
     * Add every day to the Comboboxes
     * @param days vector of the days
     */
    public void setDays(final Vector<String> days) {
        for (String day : days) {
            dayStart.getItems().add(day);
            dayStop.getItems().add(day);
        }
        dayStart.setValue(days.get(0));
        dayStop.setValue(days.get(days.size()-1));
    }

    /**
     * Create a new TypeAndQuantity for every Types and CookingStyles
     * @param vecTypes name of the types and styles
     */
    public void setTypes(final Vector<String> vecTypes) {
        types = FXCollections.observableArrayList();
        for (String s : vecTypes) {
            types.add(new TypeAndQuantityFX(s));
        }
    }

    /**
     * Initialize the TableView
     */
    public void initializeTableView() {
        tableView.setItems(types);
        typesColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("spinner"));
    }

    /**
     * Tell the controller the user has validated his choice
     */
    @SuppressWarnings("unused")
    @FXML
    private void generate() {
        listener.generateMultipleMenus(dayStart.getValue(), dayStop.getValue(), types, nbPersonSpinner.getValue());
    }

    /**
     * The listener of GenerateMultipleMenusViewController in order to communicate with the MenusDisplayController
     */
    public interface GenerateMenusListener {
        void generateMultipleMenus(String dayStart, String dayStop, ObservableList<TypeAndQuantityFX> typesAndQuantities, int nbPeople);
    }
}
