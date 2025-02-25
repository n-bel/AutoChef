package com.g04autochef.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Parent class for sub-controllers.
 */
public abstract class Controller {

    private ControllerListener mainController;

    /**
     * Global constants for all sub Controllers
     */
    public final String ICON_URL = "com/g04autochef/img/icon.png";
    public final String APP_TITLE = "AutoChef";
    public final int MIN_STAGE_WIDTH = 1400;
    public final int MIN_STAGE_HEIGHT = 720;

    protected Controller(){}

    protected Controller(ControllerListener mainController) {
        this.mainController = mainController;
    }

    /**
     * Display the View
     */
    @SuppressWarnings("unused")
    protected abstract void show();

    /**
     * @param root Parent objet of JavaFX controls
     * @param stage Window that will contain the View
     */
    private void setSizeScene(Parent root, Stage stage) {
        double previousWidth = MIN_STAGE_WIDTH;
        double previousHeight = MIN_STAGE_HEIGHT;
        Scene currentScene = stage.getScene();
        if (currentScene != null) {
            previousWidth = currentScene.getWidth();
            previousHeight = currentScene.getHeight();
        }
        stage.setScene(new Scene(root, previousWidth, previousHeight));
    }

    /**
     * Overriden from all the listeners of the sub controllers
     */
    @SuppressWarnings("unused")
    public void returnToMainMenu() {
        mainController.returnToMainMenu();
    }

    /**
     * @param stage Window that will contain the View
     * @param pathToFXML Path to window fxml
     * @return FXMLLoader Object
     * @throws IOException
     * Set the minimum Stage width and height
     * show the stage and set the icon
     */
    protected FXMLLoader getFxmlLoader(Stage stage, URL pathToFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(pathToFXML);
        Parent root = loader.load();
        setSizeScene(root, stage);
        stage.setMinWidth(MIN_STAGE_WIDTH);
        stage.setMinHeight(MIN_STAGE_HEIGHT);
        stage.setTitle(APP_TITLE);
        stage.show();
        stage.getIcons().add(new Image(ICON_URL));
        return loader;
    }

    /**
     * Listener that will be useful to communicate with the MainController
     */
    public interface ControllerListener{
        void returnToMainMenu();
    }
}
