package com.godgamer.frontend;

import java.io.IOException;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;

public class MainSceneController {
    // private Stage stage;
    // private Scene scene;
    // private Parent root;

    // accept ActionEvent e as parameter to get the source of the event such as stage, scene, etc.
    public void goToEncrypt() throws IOException {
        // Parent root = App.loadFXML("EncryptionScene");
        // stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        // scene = new Scene(root);
        // stage.setScene(scene); 
        // stage.show();
        App.setRoot("EncryptionScene");
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
}
