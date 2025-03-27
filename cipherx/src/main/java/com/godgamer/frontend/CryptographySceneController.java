package com.godgamer.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;

public class CryptographySceneController {

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
}
