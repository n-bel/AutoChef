package com.g04autochef.view.FXComponents;

import com.g04autochef.model.DayOfTheWeek;
import com.g04autochef.model.TimeOfTheDay;
import com.g04autochef.view.IntegerSpinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * Object used in the multiple generation in the Menu view
 * Displayed in a TableView
 */
public class TypeAndQuantityFX {
    public final String type;
    public final IntegerSpinner spinner;

    public TypeAndQuantityFX(String type) {
        this.type = type;
        spinner = new IntegerSpinner();
        final int MAX_NB_PEOPLE = DayOfTheWeek.values().length * TimeOfTheDay.values().length; // 7*3 = 21
        final int MIN_NB_PEOPLE = 0;
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_NB_PEOPLE, MAX_NB_PEOPLE));
        spinner.setEditable(true);
    }

    public String getType() {
        return type;
    }

    public IntegerSpinner getSpinner() {
        return spinner;
    }
}
