package com.godgamer;

import java.io.IOException;

import javafx.event.ActionEvent;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;

public class MainSceneController {
    // private Stage stage;
    // private Scene scene;
    // private Parent root;

    public void goToEncrypt(ActionEvent e) throws IOException {
        // Parent root = App.loadFXML("EncryptionScene");
        // stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        // scene = new Scene(root);
        // stage.setScene(scene); 
        // stage.show();
        App.setRoot("EncryptionScene");
    }

    public void goToDecrypt(ActionEvent e) throws IOException {
        App.setRoot("DecryptionScene");
    }
    public void goToObfuscation(ActionEvent e) throws IOException {
        App.setRoot("ObfuscationScene");
    }
    public void goToCryptography(ActionEvent e) throws IOException {
        App.setRoot("CryptographyScene");
    }
    public void goToSteganography(ActionEvent e) throws IOException {
        App.setRoot("SteganographyScene");
    }
}
