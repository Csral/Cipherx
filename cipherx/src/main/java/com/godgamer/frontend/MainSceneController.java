package com.godgamer.frontend;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;

public class MainSceneController implements Initializable {

    @FXML
    private RadioButton darkRB;
    @FXML
    private Button encryptBtn, decryptBtn, obfuscateBtn, cryptographyBtn, steganographyBtn;
    @FXML
    private ImageView mainLogo;

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

        mainLogo.setImage(App.images.get((App.isDarkMode) ? "startScreenDark" : "startScreenLight"));

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
        mainLogo.setImage(App.images.get("startScreenLight"));
        ImageView img = (ImageView) encryptBtn.getGraphic();
        img.setImage(App.images.get("encrypt"));
        img = (ImageView) decryptBtn.getGraphic();
        img.setImage(App.images.get("decrypt"));
        img = (ImageView) obfuscateBtn.getGraphic();
        img.setImage(App.images.get("obfuscate"));
        img = (ImageView) cryptographyBtn.getGraphic();
        img.setImage(App.images.get("cryptography"));
        img = (ImageView) steganographyBtn.getGraphic();
        img.setImage(App.images.get("steganography"));
        darkRB.setSelected(App.isDarkMode);
    }
}
