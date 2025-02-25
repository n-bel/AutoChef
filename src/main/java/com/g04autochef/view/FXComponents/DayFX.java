package com.g04autochef.view.FXComponents;

/**
 * Object used in the menu manager view
 * Displayed in a TableView
 */
public class DayFX {
    private final String dayName;
    private final int dayIndex;

    public DayFX(String dayName, int dayIndex) {
        this.dayName = dayName;
        this.dayIndex = dayIndex;
    }

    @SuppressWarnings("unused")
    public String getDayName() {
        return dayName;
    }

    public int getDayIndex() {
        return dayIndex;
    }
}
