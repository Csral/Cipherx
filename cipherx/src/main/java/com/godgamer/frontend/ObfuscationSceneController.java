package com.godgamer.frontend;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ObfuscationSceneController implements Initializable {

    @FXML
    private TextField inputTB, outputFileTB;
    @FXML
    private ChoiceBox<String> obfuscationTechniqueCombo;
    @FXML
    private CheckBox stringCB;
    @FXML
    private Button browseBtn, outputBrowseBtn, executeBtn;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;
    @FXML
    private CheckBox darkRB;

    private final String[] techniques = {"Base64 Encoding", "Hexadecimal Encoding", "Reverse String", "Custom Algorithm"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set up obfuscation technique combo box
        obfuscationTechniqueCombo.getItems().addAll(techniques);
        obfuscationTechniqueCombo.setValue(techniques[0]);

        // Set up advanced options
        obfuscationTechniqueCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);
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
        App.changeCSS((App.isDarkMode ? "ObfuscationDark" : "ObfuscationLight"));
    }

    private void updateAdvancedOptions(ActionEvent event) {
        String selectedTechnique = obfuscationTechniqueCombo.getValue();
        scrollContentBox.getChildren().clear();
        if (selectedTechnique != null) {
            scrollContentBox.getChildren().add(new Label("Advanced Options for " + selectedTechnique));
        }
        if (event != null) event.consume();
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

    public void getOutputFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        File selectedFile = fileChooser.showSaveDialog(App.scene.getWindow());
        if (selectedFile != null) {
            outputFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    public void executeObfuscation() {
        String technique = obfuscationTechniqueCombo.getValue();
        String input = inputTB.getText();
        String outputFile = outputFileTB.getText();

        System.out.println("Technique: " + technique);
        System.out.println("Input: " + input);
        System.out.println("Output File: " + outputFile);

        // Add logic to perform obfuscation here
    }
}