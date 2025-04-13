package com.godgamer.frontend;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;

public class CryptographySceneController implements Initializable{

    // Rtamanyu Fixed it, The error was that you had named the AnchorPane as "darkRB" in the FXML file, 
    // but it should had been the RadioButton, which is now correctly referenced.
    @FXML
    private RadioButton darkRB;

    // accept ActionEvent e as parameter to get the source of the event such as stage, scene, etc.
    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    // changes dark or light mode
    public void changeMode()
    {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS((App.isDarkMode) ? "MainSceneDark" : "MainSceneLight");     
    }

    // Rtamanyu added it for the button to save the selection even after going back to the main scene
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // set the radio button to the current mode
        darkRB.setSelected(App.isDarkMode);
    }
}
