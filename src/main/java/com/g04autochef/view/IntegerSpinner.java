package com.g04autochef.view;

import javafx.scene.control.Spinner;

/**
 * Integer only spinner box.
 * Source https://stackoverflow.com/questions/25885005/insert-only-numbers-in-spinner-control
 */
public class IntegerSpinner extends Spinner<Integer> {

    public IntegerSpinner(int min, int max, int initialValue, int amountToStepBy) {
        super(min, max, initialValue, amountToStepBy);
        init();
    }

    public IntegerSpinner() {
        init();
    }

    public void init(){
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                getEditor().setText("1");
            }
            else if(!newValue.matches("\\d*")){     // only allows integers input
                getEditor().setText(oldValue);
            }
        });
    }
}