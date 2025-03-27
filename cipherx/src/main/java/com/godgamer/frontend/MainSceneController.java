package com.godgamer.frontend;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;

public class MainSceneController implements Initializable {

    @FXML
    private RadioButton darkRB;

    // accept ActionEvent e as parameter to get the source of the event such as stage, scene, etc.
    public void goToEncrypt() throws IOException {
        App.setRoot("EncryptionScene");
    }

    // changes dark or light mode
    public void changeMode()
    {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS((App.isDarkMode) ? "MainSceneDark" : "MainSceneLight");     
    }

    public void goToDecrypt() throws IOException {
        App.setRoot("DecryptionScene");
    }
    public void goToObfuscation() throws IOException {
        App.setRoot("ObfuscationScene");
    }
    public void goToCryptography() throws IOException {
        App.setRoot("CryptographyScene");
    }
    public void goToSteganography() throws IOException {
        App.setRoot("SteganographyScene");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        darkRB.setSelected(App.isDarkMode);
    }
}
