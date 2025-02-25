package com.g04autochef.controller;

import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.Windows;
import com.g04autochef.view.map.AddHomeViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddHomeController extends Controller implements AddHomeViewController.AddHomeViewListener{
    private Stage popupStage;
    private final MapControllerListener mapControllerListener;


    public AddHomeController(final MapControllerListener mapControllerListener){
        this.mapControllerListener = mapControllerListener;
    }

    @Override
    public void saveHomeAddressAndClose(final String homeAddress) {
        mapControllerListener.setHomeAddress(homeAddress);
        close();
    }

    @Override
    public void close() {
        popupStage.close();
    }

    @Override
    protected void show() {
        popupStage = new Stage();
        try{
            FXMLLoader loader = new FXMLLoader(AddHomeViewController.class.getResource(Windows.AddHomeAddress.getPathToFXML()));
            Parent root = loader.load();
            AddHomeViewController addHomeViewController = loader.getController();
            addHomeViewController.setListener(this);
            int STAGE_HEIGHT = 383;
            int STAGE_WIDTH = 230;
            popupStage.setScene(new Scene(root, STAGE_WIDTH, STAGE_HEIGHT));
            popupStage.setResizable(false);
            popupStage.initModality(Modality.APPLICATION_MODAL);    // block primary stage while opened
            popupStage.showAndWait();
        } catch (IOException e) {
            AlertBoxGenerator.showError("Erreur du chargement de la page d'ajout de domicile", e);
        }
    }



    public interface MapControllerListener{
        void setHomeAddress(final String homeAddress);
    }
}
