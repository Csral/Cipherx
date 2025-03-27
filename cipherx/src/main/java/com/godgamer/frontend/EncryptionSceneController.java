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
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class EncryptionSceneController implements Initializable{

    @FXML
    private RadioButton darkRB;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;
    @FXML
    private ChoiceBox<String> algTypeCombo;
    @FXML
    private CheckBox verifiedAlgCB;
    @FXML
    private TextField inputTB;
    @FXML
    private TextField passwordShowTB;
    @FXML
    private PasswordField passwordTB;
    @FXML
    private Button browseBtn;

    private final String[] verifiedAlgType = {"Caesar Cipher", "Vigenere Cipher"};
    private final String[] unverifiedAlgType = {"Shift Value"};

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        browseBtn.setVisible(true);
        darkRB.setSelected(App.isDarkMode);

        // setting up algorithm type combo box
        algTypeCombo.getItems().addAll(verifiedAlgType);
        algTypeCombo.getItems().addAll(unverifiedAlgType);
        algTypeCombo.setValue(verifiedAlgType[0]);
        // setting up advanced options
        algTypeCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);
        scrollPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // setting up password fields
        passwordShowTB.setVisible(false); // Initially hidden
        // Make sure both fields have the same text
        passwordShowTB.textProperty().bindBidirectional(passwordTB.textProperty());
    }

    // accept ActionEvent e as parameter to get the source of the event such as stage, scene, etc.
    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    // changes dark or light mode
    public void changeMode()
    {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS(((App.isDarkMode) ? "EncDecDark" : "EncDecLight"));     
    }

    private void updateAdvancedOptions(ActionEvent event)
    {
        String myChoice = algTypeCombo.getValue();
        scrollContentBox.getChildren().clear();
        if (myChoice != null)
            switch(myChoice)
            {
                case "Caesar Cipher":
                    scrollContentBox.getChildren().addAll(new Label("Caesar Cipher"), new Button("click me"));
                    break;
                case "Vigenere Cipher":
                    scrollContentBox.getChildren().addAll(new Label("Vignere Cipher"), new Button("click me"));
                    break;
                default:
                    break;
            }
        if(event != null)
            event.consume();
    }

    public void togglePasswordVisibility()
    {
        passwordShowTB.setVisible(!passwordShowTB.isVisible());
        passwordTB.setVisible(!passwordTB.isVisible());
    }

    public void getFilePath()
    {
        // Create a FileChooser instance
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Set filters (Optional: restrict file types)
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        // Open file dialog and get the selected file
        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());

        // Check if a file was selected and print the path
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            inputTB.setText(selectedFile.getAbsolutePath());
        } else {
            System.out.println("No file selected.");
        }
    }

    public void changeAlgorithmList()
    {
        if(verifiedAlgCB.isSelected())
        {
            algTypeCombo.getItems().clear();
            algTypeCombo.getItems().addAll(verifiedAlgType);
        }
        else
        {
            algTypeCombo.getItems().clear();
            algTypeCombo.getItems().addAll(verifiedAlgType);           
            algTypeCombo.getItems().addAll(unverifiedAlgType);        
        }
        algTypeCombo.setValue(verifiedAlgType[0]);
    }

    public void toggleBrowseBtn()
    {
        inputTB.setText("");
        browseBtn.setVisible(!browseBtn.isVisible());
    }
}
