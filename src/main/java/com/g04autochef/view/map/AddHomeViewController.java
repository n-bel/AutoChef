package com.g04autochef.view.map;

import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class AddHomeViewController extends ViewController {


    private AddHomeViewListener addHomeController;

    @FXML private TextField addressStreet;
    @FXML private TextField addressNumber;
    @FXML private TextField addressPostalCode;

    /**
     * Disregards changes and closes stage.
     */
    @FXML private void closePopup(){
        addHomeController.close();
    }

    @FXML private void addAddress(){
        if (aFieldIsEmpty()) {
            alertEmptyField();
        }
        else{
            final String address = getAddress();
            addHomeController.saveHomeAddressAndClose(address);
        }
    }

    /**
     * {@return True if any of the fields are empty to allow to add home address.}
     */
    private boolean aFieldIsEmpty(){
        final boolean fieldStreetIsEmpty = addressStreet.getText().trim().isEmpty();
        final boolean fieldNumberIsEmpty = addressPostalCode.getText().trim().isEmpty();
        final boolean fieldPostalCodeIsEmpty = addressPostalCode.getText().trim().isEmpty();
        final boolean isAFieldEmpty = fieldStreetIsEmpty || fieldNumberIsEmpty || fieldPostalCodeIsEmpty;
        return isAFieldEmpty;
    }
    private static void alertEmptyField(){
        AlertBoxGenerator.showWarning("Un champ est vide !", "Veuillez remplir tout les champs");
    }

    /**
     * {@return The address that the user has written}
     */
    private String getAddress(){
        final String SPACE = " ";
        final String address = addressStreet.getText() + SPACE + addressNumber.getText()
                + SPACE + addressPostalCode.getText();
        return address;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * Set the controller as listener
     * @param listener controller implementing the AddHomeViewListener interface
     */
    public void setListener(AddHomeViewListener listener) {
        this.addHomeController = listener;
    }

    public interface AddHomeViewListener{
       void saveHomeAddressAndClose(final String homeAddress);
       void close();
    }


}
