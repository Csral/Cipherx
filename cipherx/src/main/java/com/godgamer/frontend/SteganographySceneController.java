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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SteganographySceneController implements Initializable {

    @FXML private RadioButton encodeRadio, decodeRadio;
    @FXML private RadioButton darkRB;

    @FXML private CheckBox stringCB;

    @FXML private TextField keyTB, carrierFileTB, outputFileTB;

    @FXML private TextArea messageFileTB;

    @FXML private Button carrierBrowseBtn, messageBrowseBtn, outputBrowseBtn, generateKeyBtn, executeBtn;

    @FXML private ChoiceBox<String> algTypeCombo;

    @FXML private ScrollPane scrollPanel;
    @FXML private VBox scrollContentBox;

    private final String[] algorithms = {"LSB", "DCT", "Custom"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup operationGroup = new ToggleGroup();
        encodeRadio.setToggleGroup(operationGroup);
        decodeRadio.setToggleGroup(operationGroup);
        encodeRadio.setSelected(true);

        algTypeCombo.getItems().addAll(algorithms);
        algTypeCombo.setValue(algorithms[0]);
        algTypeCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);

        carrierBrowseBtn.setOnAction(this::getCarrierFilePath);
        messageBrowseBtn.setOnAction(this::getMessageFilePath);
        outputBrowseBtn.setOnAction(this::getOutputFilePath);

        darkRB.setSelected(App.isDarkMode);
        darkRB.setOnAction(e -> changeMode());

        keyTB.setText("");

        // call no-arg toggle to set correct initial visibility
        toggleMessageType();
        stringCB.setOnAction(this::toggleMessageType);
    }

    @FXML
    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    @FXML
    public void changeMode() {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS(App.isDarkMode ? "EncDecDark" : "EncDecLight");
    }

    @FXML
    private void updateAdvancedOptions(ActionEvent event) {
        String selectedAlgorithm = algTypeCombo.getValue();
        scrollContentBox.getChildren().clear();

        if (selectedAlgorithm != null) {
            switch (selectedAlgorithm) {
                case "LSB":
                    scrollContentBox.getChildren().add(new Label("No advanced options for LSB."));
                    break;
                case "DCT":
                    scrollContentBox.getChildren().addAll(
                        new Label("DCT Compression Level (0–100):"),
                        new TextField()
                    );
                    break;
                case "Custom":
                    scrollContentBox.getChildren().addAll(
                        new CheckBox("Embed in Alpha Channel"),
                        new CheckBox("Compress Message"),
                        new CheckBox("Encrypt with Key")
                    );
                    break;
                default:
                    scrollContentBox.getChildren().add(new Label("No advanced options available."));
            }
        }

        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);
        if (event != null) event.consume();
    }

    @FXML
    public void toggleMessageType(ActionEvent event) {
        toggleMessageType(); // delegate to no-arg version
    }

    public void toggleMessageType() {
        boolean isString = stringCB.isSelected();
        messageBrowseBtn.setVisible(!isString);

        if (isString) {
            messageFileTB.setPromptText("Enter message text");
            messageFileTB.setText("");
        } else {
            messageFileTB.setPromptText("Message will be read from file...");
        }
    }

    @FXML
    public void getCarrierFilePath(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Carrier File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = chooser.showOpenDialog(App.scene.getWindow());
        if (file != null) {
            carrierFileTB.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void getMessageFilePath(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Message File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = chooser.showOpenDialog(App.scene.getWindow());
        if (file != null) {
            messageFileTB.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void getOutputFilePath(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Output File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = chooser.showSaveDialog(App.scene.getWindow());
        if (file != null) {
            outputFileTB.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void generateKey(ActionEvent event) {
        keyTB.setText("AutoKey2025"); // Placeholder
    }

    @FXML
    public void executeStegoOperation(ActionEvent event) {
        String operation = encodeRadio.isSelected() ? "Encode" : "Decode";
        String algorithm = algTypeCombo.getValue();
        String key = keyTB.getText();
        String carrierFile = carrierFileTB.getText();
        String output = outputFileTB.getText();
        boolean useStringInput = stringCB.isSelected();
        String message = messageFileTB.getText();

        System.out.println("=== Steganography Operation ===");
        System.out.println("Operation: " + operation);
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Key: " + key);
        System.out.println("Carrier File: " + carrierFile);
        System.out.println("Message: " + message + (useStringInput ? " (String)" : " (File Path)"));
        System.out.println("Output File: " + output);

        if (operation.equals("Encode")) {
            System.out.println("Encoding message into file...");
        } else {
            System.out.println("Decoding message from file...");
        }

        // TODO: Add actual steganography logic here
    }
}
