package com.godgamer.frontend;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.godgamer.backend.obfuscation.Hiding;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ObfuscationSceneController implements Initializable {

    @FXML private RadioButton obfuscateRadio;
    @FXML private RadioButton deobfuscateRadio;
    @FXML private ToggleGroup operationGroup;
    @FXML private Label dataLabel;

    // @FXML private ChoiceBox<String> algTypeCombo;
    @FXML private TextField carrierFileTB, keyShowTB, dataValueTB;
    @FXML private Button carrierBrowseBtn;
    @FXML private PasswordField keyTB;
    @FXML private Button executeBtn, showKeyBtn;
    @FXML private ScrollPane scrollPanel;
    @FXML private RadioButton darkRB;

    // private final String[] algorithms = {"Meta-Hiding"};

    private final Hiding hidingTool = new Hiding();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // algTypeCombo.getItems().addAll(algorithms);
        // algTypeCombo.setValue(algorithms[0]);

        darkRB.setSelected(App.isDarkMode);
        darkRB.setOnAction(e -> changeMode());

        carrierBrowseBtn.setOnAction(this::getCarrierFilePath);

        ToggleGroup group = new ToggleGroup();
        obfuscateRadio.setToggleGroup(group);
        deobfuscateRadio.setToggleGroup(group);
        obfuscateRadio.setSelected(true); // Default
        keyShowTB.setVisible(false);
        // Make sure both fields have the same text
        keyShowTB.textProperty().bindBidirectional(keyTB.textProperty());
        ImageView showBtnImg = (ImageView)showKeyBtn.getGraphic();
        showBtnImg.setImage(App.images.get("show pass")); // Set the image to show password icon
    }

    public void showKey()
    {
        keyShowTB.setVisible(!keyShowTB.isVisible());
        keyTB.setVisible(!keyTB.isVisible());
        ImageView showBtnImg = (ImageView) showKeyBtn.getGraphic();
        if (keyShowTB.isVisible())
            showBtnImg.setImage(App.images.get("hide pass")); // Set the image to hide password icon
        else
            showBtnImg.setImage(App.images.get("show pass")); // Set the image to show password icon
    }

    public void getCarrierFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());
        if (selectedFile != null) {
            carrierFileTB.setText(selectedFile.getAbsolutePath());
        }
    }

    public void changeMode() {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS((App.isDarkMode ? "EncDecDark" : "EncDecLight"));
    }

    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    public void executeOperation() {
        // String algorithm = algTypeCombo.getValue();
        String carrierFile = carrierFileTB.getText();
        String key = keyTB.getText();

        if (carrierFile == null || carrierFile.isEmpty() || key == null || key.isEmpty()) {
            Logger.showError("Missing Fields", "Input file and key are required.", "Please fill all required fields.");
            return;
        }

        // if ("Meta-Hiding".equals(algorithm)) {
            boolean isObfuscate = obfuscateRadio.isSelected();

            int resultCode;
            String resultMessage;

            if (isObfuscate) {
                resultCode = hidingTool.streamMetaData(carrierFile, key, dataValueTB.getText());
                resultMessage = (resultCode == 0) ? "Obfuscation successful." : "Obfuscation failed.";
            } else {
                String extracted = hidingTool.extractMetaData(carrierFile, key);
                resultMessage = (extracted != null) ? "Deobfuscated Data: \n" + extracted : "Failed to extract metadata.";
            }

            showOutput(resultMessage);
        // } else {
            // showOutput("Unsupported algorithm selected.");
        // }
    }

    private void showOutput(String message) {
        TextArea textArea = new TextArea(message);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        
        // Let it grow inside the scroll pane
        textArea.setPrefWidth(scrollPanel.getWidth());
        textArea.setPrefHeight(scrollPanel.getHeight());
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        scrollPanel.setContent(textArea);
    }    

    public void toggleKey(){
        if(obfuscateRadio.isSelected()){
            dataValueTB.setVisible(true);
            dataLabel.setVisible(true);
        }
        else{
            dataValueTB.setVisible(false);
            dataLabel.setVisible(false);
        }
    }
}
