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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class CryptographySceneController implements Initializable {

    // RadioButtons for operation type
    @FXML
    private RadioButton encryptRadio, decryptRadio;

    // CheckBox for dark mode
    @FXML
    private RadioButton darkRB;

    // TextFields for input, key, carrier file, and output file
    @FXML
    private TextField keyTB, carrierFileTB, outputFileTB;

    // ChoiceBox for algorithm selection
    @FXML
    private ChoiceBox<String> algTypeCombo;

    // CheckBox for toggling between string and file input
    @FXML
    private CheckBox stringCB;

    // Buttons for file browsing and key generation
    @FXML
    private Button carrierBrowseBtn, outputBrowseBtn, generateKeyBtn, executeBtn;

    // ScrollPane and VBox for advanced options
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;

    // List of algorithms
    private final String[] algorithms = {"AES", "RSA", "DES", "Blowfish"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set up ToggleGroup for RadioButtons
        ToggleGroup operationType = new ToggleGroup();
        encryptRadio.setToggleGroup(operationType);
        decryptRadio.setToggleGroup(operationType);
        encryptRadio.setSelected(true);

        // Set up algorithm type combo box
        algTypeCombo.getItems().addAll(algorithms);
        algTypeCombo.setValue(algorithms[0]);

        // Set up advanced options
        algTypeCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);

        // Set up dark mode toggle
        darkRB.setSelected(App.isDarkMode);
        darkRB.setOnAction(event -> changeMode());

        // Set up password fields (if applicable)
        if (keyTB != null) {
            keyTB.setText("");
        }

        // Set up file chooser buttons
        //browseBtn.setVisible(!stringCB.isSelected());
        //inputTB.setPromptText(stringCB.isSelected() ? "Enter Text" : "Select File Path");
        // browseBtn.setOnAction(this::getFilePath);
        carrierBrowseBtn.setOnAction(this::getCarrierFilePath);
        outputBrowseBtn.setOnAction(this::getOutputFilePath);
        carrierFileTB.setPromptText("Select Carrier File");
        outputFileTB.setPromptText("Select Carrier File");
    }

    /**
     * Navigates back to the main scene.
     */
    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    /**
     * Toggles between dark and light modes.
     */
    public void changeMode() {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS((App.isDarkMode ? "EncDecDark" : "EncDecLight"));
    }

    /**
     * Updates the advanced options based on the selected algorithm.
     */
    private void updateAdvancedOptions(ActionEvent event) {
        String selectedAlgorithm = algTypeCombo.getValue();
        scrollContentBox.getChildren().clear();
        if (selectedAlgorithm != null) {
            switch (selectedAlgorithm) {
                case "AES":
                    scrollContentBox.getChildren().addAll(new Label("AES Key Size"), new TextField());
                    break;
                case "RSA":
                    scrollContentBox.getChildren().addAll(new Label("RSA Key Pair"), new Button("Generate Keys"));
                    break;
                default:
                    scrollContentBox.getChildren().add(new Label("No advanced options available."));
            }
        }
        if (event != null) event.consume();
    }

    /**
     * Toggles the visibility of the browse button based on the string/file toggle.
     */
    public void toggleInputType() {
        //browseBtn.setVisible(!stringCB.isSelected());
        // inputTB.setPromptText(stringCB.isSelected() ? "Enter Text" : "Select File Path");
        carrierBrowseBtn.setVisible(!stringCB.isSelected());
        outputBrowseBtn.setVisible(!stringCB.isSelected());
        if (stringCB.isSelected()) {
            carrierFileTB.setText("");
            outputFileTB.setText("");
            
            carrierFileTB.setPromptText("Enter Carrier String");
            outputFileTB.setPromptText("Enter Output String");
        } else {
            carrierFileTB.setPromptText("Select Carrier File");
            outputFileTB.setPromptText("Select Carrier File");
        }
    }

    /**
     * Opens a file chooser to select an input file.
     */
    public void getFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());
        if (selectedFile != null) {
            //inputTB.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Opens a file chooser to select a carrier file.
     */
    public void getCarrierFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Carrier File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());
        if (selectedFile != null) {
            carrierFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Opens a file chooser to save the output file.
     */
    public void getOutputFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Output File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(App.scene.getWindow());
        if (selectedFile != null) {
            outputFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Generates a cryptographic key (placeholder logic).
     */
    public void generateKey() {
        keyTB.setText("GeneratedKey123"); // Placeholder for actual key generation
    }

    /**
     * Executes the cryptographic operation (encryption/decryption).
     */
    public void executeOperation() {
        String operation = encryptRadio.isSelected() ? "Encrypt" : "Decrypt";
        String algorithm = algTypeCombo.getValue();
        // String input = inputTB.getText();
        String key = keyTB.getText();
        String carrierFile = carrierFileTB.getText();
        String outputFile = outputFileTB.getText();

        System.out.println("Operation: " + operation);
        System.out.println("Algorithm: " + algorithm);
        //System.out.println("Input: " + input);
        //System.out.println("Key: " + key);
        System.out.println("Carrier File: " + carrierFile);
        System.out.println("Output File: " + outputFile);

        // Add logic to perform encryption/decryption here
    }
}