package com.godgamer.frontend;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SteganographySceneController implements Initializable {

    @FXML
    private RadioButton embedRadio, extractRadio, darkRB;
    @FXML
    private TextField inputTB, carrierFileTB, outputFileTB;
    @FXML
    private CheckBox stringCB;
    @FXML
    private Button browseBtn, carrierBrowseBtn, outputBrowseBtn, executeBtn;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set up ToggleGroup for RadioButtons
        ToggleGroup operationType = new ToggleGroup();
        embedRadio.setToggleGroup(operationType);
        extractRadio.setToggleGroup(operationType);
        embedRadio.setSelected(true);

        // Set up advanced options
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);

        // Set up dark mode toggle
        darkRB.setSelected(App.isDarkMode);
        darkRB.setOnAction(event -> changeMode());
    }

    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    public void changeMode() {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS((App.isDarkMode ? "SteganographyDark" : "SteganographyLight"));
    }

    public void toggleInputType() {
        browseBtn.setVisible(!stringCB.isSelected());
        inputTB.setPromptText(stringCB.isSelected() ? "Enter Text" : "Select File Path");
    }

    public void getFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());
        if (selectedFile != null) {
            inputTB.setText(selectedFile.getAbsolutePath());
        }
    }

    public void getCarrierFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Carrier File");
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());
        if (selectedFile != null) {
            carrierFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    public void getOutputFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        File selectedFile = fileChooser.showSaveDialog(App.scene.getWindow());
        if (selectedFile != null) {
            outputFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    public void executeSteganography() {
        String operation = embedRadio.isSelected() ? "Embed" : "Extract";
        String input = inputTB.getText();
        String carrierFile = carrierFileTB.getText();
        String outputFile = outputFileTB.getText();

        System.out.println("Operation: " + operation);
        System.out.println("Input: " + input);
        System.out.println("Carrier File: " + carrierFile);
        System.out.println("Output File: " + outputFile);

        // Add logic to perform steganography here
    }
}