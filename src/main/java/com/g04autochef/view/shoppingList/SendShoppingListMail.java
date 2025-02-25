package com.g04autochef.view.shoppingList;

import com.g04autochef.view.AlertBoxGenerator;
import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;
import java.util.Vector;

public class SendShoppingListMail extends ViewController {

    @FXML private SendShoppingListMailListener shoppingListController;
    @FXML private ComboBox<String> pdfComboBox;
    @FXML private TextField nameShoppingListTextField;
    @FXML private TextField emailDestination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailDestination.textProperty().addListener((v, oldText, newText) -> {
            if (newText != null && !oldText.equals(newText)){
                updateTextFieldStyle();
            }
        });
    }

    /**
     * Highlight in red the textfield if the email is not correct
     */
    private void updateTextFieldStyle() {
        if (isValidEmailAddress()) {
            this.emailDestination.setStyle("-fx-border-color: LIGHTSTEELBLUE;");
        }
        else {
            this.emailDestination.setStyle("-fx-border-color: RED;");
        }
    }

    public void setListener(final SendShoppingListMailListener shoppingListController) {
        this.shoppingListController = shoppingListController;
    }

    public void initializeComboBoxListener(){
        pdfComboBox.getSelectionModel().selectedItemProperty().addListener(
                (v, oldValue, newValue) -> nameShoppingListTextField.setText(shoppingListController.updateShoppingListNameEmail(newValue)));
    }

    /**
     * {@return True is the email address is valid}
     * Source : https://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
     */
    public boolean isValidEmailAddress() {
        boolean result = true;
        try {
            final InternetAddress emailAddr = new InternetAddress(this.emailDestination.getText());
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    @FXML
    private void comfirmSending() throws GeneralSecurityException {
        String email = String.valueOf(emailDestination.getText());
        if (isValidEmailAddress())
            shoppingListController.confirmSending(pdfComboBox.getValue(), email);
        else{
            AlertBoxGenerator.showWarning("L'adresse mail est invalide","");
        }
    }

    @FXML
    private void cancelSending(){
        shoppingListController.cancelSending();
    }

    /**
     * Initialize the combobox with all the pdfs
     * @param pdfNamesVector the vector of all the pdf names
     */
    public void initializePDFComboBox(Vector<String> pdfNamesVector){
        pdfComboBox.getItems().addAll(pdfNamesVector);
    }

    public void setCurrentShoppingListName(String shoppingListName) {
        nameShoppingListTextField.setText(shoppingListName);
        String pdfName = shoppingListName.replaceAll(" ","");
        pdfComboBox.setValue(pdfName+".pdf");
    }

    public interface SendShoppingListMailListener{
        void confirmSending(String pdfName, String emailDestination) throws GeneralSecurityException;
        void cancelSending();
        String updateShoppingListNameEmail(String pdfName);
    }
}
