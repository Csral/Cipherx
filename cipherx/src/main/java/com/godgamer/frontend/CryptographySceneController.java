package com.godgamer.frontend;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.godgamer.backend.Cryptography.Cryptographer;

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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class CryptographySceneController implements Initializable {

    // CheckBox for dark mode
    @FXML
    private RadioButton darkRB;

    // TextFields for input, key, carrier file, and output file
    @FXML
    private TextField carrierFileTB;

    // ChoiceBox for algorithm selection
    @FXML
    private ChoiceBox<String> algTypeCombo;

    // CheckBox for toggling between string and file input
    @FXML
    private CheckBox stringCB;

    // Buttons for file browsing and key generation
    @FXML
    private Button carrierBrowseBtn, executeBtn;

    // ScrollPane and VBox for advanced options
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;

    // List of algorithms
    private final String[] algorithms = {"MD2", "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set up algorithm type combo box
        algTypeCombo.getItems().addAll(algorithms);
        algTypeCombo.setValue(algorithms[0]);
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);

        // Set up dark mode toggle
        darkRB.setSelected(App.isDarkMode);
        darkRB.setOnAction(event -> changeMode());

        // Set up file chooser buttons
        //browseBtn.setVisible(!stringCB.isSelected());
        //inputTB.setPromptText(stringCB.isSelected() ? "Enter Text" : "Select File Path");
        // browseBtn.setOnAction(this::getFilePath);
        carrierBrowseBtn.setOnAction(this::getCarrierFilePath);
        carrierFileTB.setPromptText("Select Carrier File");
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
     * Toggles the visibility of the browse button based on the string/file toggle.
     */
    public void toggleInputType() {
        //browseBtn.setVisible(!stringCB.isSelected());
        // inputTB.setPromptText(stringCB.isSelected() ? "Enter Text" : "Select File Path");
        carrierBrowseBtn.setVisible(!stringCB.isSelected());
        if (stringCB.isSelected()) {
            carrierFileTB.setText("");
            carrierFileTB.setPromptText("Enter Carrier String");
        } else {
            carrierFileTB.setPromptText("Select Carrier File");
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
     * Executes the cryptographic operation (encryption/decryption).
     */
    public void executeOperation() {
        scrollContentBox.getChildren().clear();
        String algorithm = algTypeCombo.getValue();
        String output;
        String input = "";
        if (carrierBrowseBtn.isVisible()){
            input = carrierFileTB.getText();
            File file = new File(input);
            if (!file.exists()) {
                Logger.showError("File not found", "File not found", "File not found");
                return;
            }
                try {
                input = Files.readString(Path.of(input));
            } catch (IOException e) {
                Logger.showError("Error while reading File", "Error while reading File", "Error while reading File");
                e.printStackTrace();
            }
        } else {
            input = carrierFileTB.getText();
        }
        switch (algorithm) {
            case "MD2":
                output = Cryptographer.verified(input, "MD2");
                break;
            case "MD5":
                output = Cryptographer.verified(input, "MD5");
                break;
            case "SHA-1":
                output = Cryptographer.verified(input, "SHA-1");
                break;
            case "SHA-224":
                output = Cryptographer.verified(input, "SHA-224");
                break;
            case "SHA-256":
                output = Cryptographer.verified(input, "SHA-256");
                break;
            case "SHA-384":
                output = Cryptographer.verified(input, "SHA-384");
                break;
            case "SHA-512":
                output = Cryptographer.verified(input, "SHA-512");
                break;
            default:
                output = "Not Supported Algorithm";
        }
        scrollContentBox.getChildren().add(new Label(output));
        // String input = inputTB.getText();
        String carrierFile = carrierFileTB.getText();
        System.out.println("Algorithm: " + algorithm);
        //System.out.println("Input: " + input);
        //System.out.println("Key: " + key);
        System.out.println("Carrier File: " + carrierFile);

        // Add logic to perform encryption/decryption here
    }
}