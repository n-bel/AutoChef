package com.g04autochef.view.shoppingList;

import com.g04autochef.view.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class DisplayQrCodeViewController extends ViewController {

    @FXML private ImageView imageDisplayed;
    @FXML private Label titleLabel;

    private DisplayQRCodeListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setListener(final DisplayQRCodeListener listener) {
        this.listener = listener;
    }

    public void closeWindow() {
        listener.closeWindow();
    }

    public void setImage(Image image) {
        imageDisplayed.setImage(image);
        imageDisplayed.setPreserveRatio(false);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Interface for listener.
     */
    public interface DisplayQRCodeListener {
        void closeWindow();
    }
}
