package com.g04autochef.view;

import javafx.scene.control.Alert;

public final class AlertBoxGenerator {
    /**
     * Show an alert with the specified message
     * @param title alert title
     * @param header alert header
     * @param content alert content
     */
    private static void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showWarning(final String header, final String content){
        final String title = "Warning";
        showAlert(title, header, content, Alert.AlertType.WARNING);
    }

    /**
     * Show alert box for an Error,
     * also prints error stacktrace to console for more detailed view.
     */
    public static void showError(final String header, final Exception e){
        final String title = "Error";
        showAlert(title, header, e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }

    public static void showInfo(final String header, final String content){
        final String title = "Info";
        showAlert(title, header, content, Alert.AlertType.INFORMATION);
    }


}
