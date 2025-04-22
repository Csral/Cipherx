package com.godgamer.frontend;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DecryptionSceneController implements Initializable {

    @FXML
    private RadioButton darkRB;
    @FXML
    private ScrollPane scrollPanel;
    @FXML
    private VBox scrollContentBox;
    @FXML
    private ChoiceBox<String> algTypeCombo;
    @FXML
    private TextField inputTB, passwordShowTB, outputTB, keyFileTB;
    @FXML
    private PasswordField passwordTB;
    @FXML
    private Button browseBtn, showBtn;

    private String selectedExt = null;

    private class Algorithms 
    {
        public String algName;
        public Map<String, Node> algOptions = new HashMap<>(); // this will be used to store the advanced options for the algorithm
        public Algorithms(String algName) {
            this.algName = algName;
        }
        public Algorithms addOptions(HashMap<String, Node> nodes) {
            algOptions.putAll(nodes);
            return this;
        }
    }

     // filter to allow only integer values in the text field
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?\\d*")) { // allows optional negative sign and digits
            return change;
        }
        return null;
    };

    private final Algorithms[] verifiedAlgType = {
        new Algorithms("CBC").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("ECB").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("CFB").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("OFB").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("CTR").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("GCM").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("ChaCha20").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("Poly1305").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
            }
        }),
        new Algorithms("RSA").addOptions(new HashMap<String, Node>() {
            {
                put("No Advanced Options", new Label(""));
                // Button browseBtn = new Button("Browse");
                // browseBtn.setOnAction(e -> getKeyFilePath());
                // TextField keyFileTB = new TextField();
                // keyFileTB.setPromptText("Private Key File Path..");
                // HBox keyFileBox = new HBox(10, keyFileTB, browseBtn);
                // keyFileBox.setAlignment(Pos.CENTER_LEFT);
                // put("Private Key", keyFileBox);
            }
        }),
    }, unverifiedAlgType = {};

    private static Algorithms curAlg = null; // this will be used to store the current algorithm selected

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        browseBtn.setVisible(true);
        darkRB.setSelected(App.isDarkMode);

        // setting up algorithm type combo box
        for (Algorithms alg : verifiedAlgType) {
            algTypeCombo.getItems().add(alg.algName);
        }
        for (Algorithms alg : unverifiedAlgType) {
            algTypeCombo.getItems().add(alg.algName);
        }
        algTypeCombo.setValue(verifiedAlgType[0].algName);

        algTypeCombo.setOnAction(this::updateAdvancedOptions);
        updateAdvancedOptions(null);
        scrollPanel.setContent(scrollContentBox);
        scrollPanel.setFitToWidth(true);
        scrollPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        passwordShowTB.setVisible(false); // Initially hidden
        passwordShowTB.textProperty().bindBidirectional(passwordTB.textProperty());
        ImageView showBtnImg = (ImageView)showBtn.getGraphic();
        showBtnImg.setImage(App.images.get("show pass")); // Set the image to show password icon
    }

    public void backToMain() throws Exception {
        App.setRoot("MainScene", "MainSceneDark", "MainSceneLight");
    }

    public void changeMode()
    {
        App.isDarkMode = !App.isDarkMode;
        darkRB.setSelected(App.isDarkMode);
        App.changeCSS(((App.isDarkMode) ? "EncDecDark" : "EncDecLight"));     
    }

    // To get the required element from the advanced options
    private  Node getOptionValue(Algorithms alg, String optionKey) {
        if (alg != null && alg.algOptions.containsKey(optionKey)) {
            return alg.algOptions.get(optionKey);
        }

        return null;
    }

    private void updateAdvancedOptions(ActionEvent event)
    {
        String myChoice = algTypeCombo.getValue();
        scrollContentBox.getChildren().clear();

        Algorithms selectedAlg = null;

        for (Algorithms alg : verifiedAlgType) {
            if (alg.algName.equals(myChoice)) {
                selectedAlg = alg;
                break;
            }
        }
        if (selectedAlg == null) {
            for (Algorithms alg : unverifiedAlgType) {
                if (alg.algName.equals(myChoice)) {
                    selectedAlg = alg;
                    break;
                }
            }
        }

        if (selectedAlg != null) {
            curAlg = selectedAlg; // Store the current algorithm
            for (Map.Entry<String, Node> entry : selectedAlg.algOptions.entrySet()) {
                String key = entry.getKey();
                Node valueNode = entry.getValue();

                // Create a row with key label and input node
                HBox row = new HBox(10); // spacing between label and input
                Label keyLabel = new Label(key);
                row.getChildren().addAll(keyLabel, valueNode);
                scrollContentBox.setSpacing(4); // adds vertical space between HBox rows
                row.setAlignment(Pos.CENTER_LEFT); // aligns children to the left
                keyLabel.setMinWidth(120); // set consistent width for labels
                scrollContentBox.getChildren().add(row);
            }

            if(selectedAlg.algName.equals("RSA")) {
                keyFileTB.setPromptText("Enter the Private Key File Path..");
                keyFileTB.setText("");
            } 
            else {
                keyFileTB.setPromptText("Enter the Key File Path..");
            }
        }

        if (event != null)
            event.consume();
    }

    public void togglePasswordVisibility()
    {
        passwordShowTB.setVisible(!passwordShowTB.isVisible());
        passwordTB.setVisible(!passwordTB.isVisible());
        ImageView showBtnImg = (ImageView) showBtn.getGraphic();
        if (passwordShowTB.isVisible())
            showBtnImg.setImage(App.images.get("hide pass"));
        else
            showBtnImg.setImage(App.images.get("show pass"));
    }

    public void getFilePath()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // fileChooser.getExtensionFilters().add(
        //     new FileChooser.ExtensionFilter("Binary File", "*.dat")
        // );

        File selectedFile = fileChooser.showOpenDialog(App.scene.getWindow());

        if (selectedFile != null) {
            selectedExt = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf("."));
            Logger.printMessage("Selected file: " + selectedFile.getAbsolutePath());
            inputTB.setText(selectedFile.getAbsolutePath());
        } else {
            Logger.printMessage("No file selected.");
        }
    }

    /**
     * * This method is used to get the output file path from the user using a
     * FileChooser dialog. * * It sets the selected file path to the outputTB
     * TextField. * * If the user cancels the operation, it prints a message to
     * the console.
     */
    public void getOutputFilePath() {
        if (selectedExt == null) {
            Logger.showWarning("Kindly select an input file first!", "Input File not specified", "Input is empty!");
            return;
        }
        // Create a FileChooser instance
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save a file");

        // Set filters (Optional: restrict file types)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File", "*" + selectedExt)
        );

        // Show the save file dialog
        File fileToSave = fileChooser.showSaveDialog(App.scene.getWindow());

        if (fileToSave != null) {
            Logger.printMessage("File to save: " + fileToSave.getAbsolutePath());
            outputTB.setText(fileToSave.getAbsolutePath());
        } else {
            Logger.printMessage("Save operation cancelled.");
        }
    }

    /**
     * * This method is used to get the key file path from the user using a
     * FileChooser dialog. * * It sets the selected file path to the keyFileTB
     * TextField. * * If the user cancels the operation, it prints a message to
     * the console.
     */
    public void getKeyFilePath() {
        if(curAlg.algName.equals("RSA") && passwordTB.getText().isEmpty()) {
            Logger.showWarning("Kindly enter the password for the private key!", "Password not specified", "Password is empty!");
            return;
        }
        // Create a FileChooser instance
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(curAlg.algName.equals("RSA") ? "Open Private Key File" : "Open Key File");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Key File", "*.key")
        );

        // Show the open file dialog
        File fileToOpen = fileChooser.showOpenDialog(App.scene.getWindow());

        if (fileToOpen != null) {
            Logger.printMessage("File to open: " + fileToOpen.getAbsolutePath());
            keyFileTB.setText(fileToOpen.getAbsolutePath());
            if(curAlg.algName.equals("RSA"))
            {
                App.rsa.KEY_LOAD_PRIVATE(fileToOpen.getAbsolutePath(), true, passwordTB.getText());
                Logger.showInfo("Private Key Loaded Successfully!", "Private Key Loaded", "Private Key Loaded Successfully!"); 
            }
        } else {
            Logger.printMessage("Open operation cancelled.");
        }
    }

    private void resetWindow() {
        inputTB.setText("");
        passwordTB.setText("");
        passwordShowTB.setText("");
        keyFileTB.setText("");
        outputTB.setText("");
        algTypeCombo.setValue(verifiedAlgType[0].algName);
        updateAdvancedOptions(null);
    }

    public void execute()
    {
        byte[] inputData;
        String password = passwordTB.getText();

        if(inputTB.getText().isEmpty())
        {    
            Logger.showWarning("Kindly browse for an input file or enter the message to be encrypted!", "Input File not specified", "Input is empty!");
            return;
        }
        if(outputTB.getText().isEmpty())
        {     
            Logger.showWarning("Kindly specify the location of output file!", "Output File not specified", "Output is empty!");
            return;
        }
  
        File inputFile = new File(inputTB.getText());
        if (!inputFile.exists()) {
            Logger.showWarning("Kindly browse for an existing input file", "Input file does not exist", "Input file does not exist!");
            return;
        }
        if (!inputFile.canRead()) {
            Logger.showWarning("Kindly browse for a readable input file", "Input file is not readable", "Input file is not readable!");
            return;
        }
        // Read the file content into a byte array
        try {
            inputData = java.nio.file.Files.readAllBytes(inputFile.toPath());
            if (inputData.length == 0) {
                Logger.showWarning("Kindly browse for a non-empty input file", "Input file is empty", "Input file is empty!");
                return;
            }
            Logger.printMessage("Input file read successfully!");
        } catch (java.io.IOException e) {
            Logger.showError("An error encountered while reading input file!", "Input File Error", "Error reading input file: " + e.getMessage());
            return;
        }

        // for rsa
        if (curAlg.algName.equals("RSA") && !App.rsa.isPrivateKeyLoaded()) {
            Logger.showWarning("Please load the private key before proceeding.", "Private Key Not Loaded", "Private key is not loaded!");
            return;
        }

        Logger.printMessage("Decrypting Started..");
        // Create progress dialog
        Stage progressDialog = new Stage();
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.initOwner(App.scene.getWindow());
        progressDialog.setTitle("Decryption in Progress");
        progressDialog.setResizable(false);
        // progressDialog.setAlwaysOnTop(true);
        VBox vbox = new VBox(10, new Label("Decrypting... Please wait."), new ProgressIndicator());
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(vbox, 300, 100);
        progressDialog.setScene(scene);

        // Create encryption task
        Task<byte[]> decryptionTask = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                // Perform encryption here
                String filename = new File(keyFileTB.getText()).getName();
                switch (curAlg.algName) {
                    case "CBC":
                        if (password.isEmpty())
                            return App.aes.decrypt_CBC(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_CBC(inputData, filename, password);
                        
                    case "ECB": 
                        if (password.isEmpty())
                            return App.aes.decrypt_ECB(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_ECB(inputData, filename, password);
                    case "CFB":
                        if (password.isEmpty())
                            return App.aes.decrypt_CFB(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_CFB(inputData, filename, password);
                    case "OFB":
                        if (password.isEmpty())
                            return App.aes.decrypt_OFB(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_OFB(inputData, filename, password);
                    case "CTR":
                        if (password.isEmpty())
                            return App.aes.decrypt_CTR(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_CTR(inputData, filename, password);
                    case "GCM":
                        if (password.isEmpty())
                            return App.aes.decrypt_GCM(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.aes.decrypt_GCM(inputData, filename, password);
                    case "ChaCha20":
                        if (password.isEmpty())
                            return App.chacha20.decrypt(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.chacha20.decrypt(inputData, filename, password);
                    case "Poly1035":
                        if (password.isEmpty())
                            return App.chacha20.decrypt_poly1305(inputData, filename); // Ensure this method returns encrypted bytes
                        else
                            return App.chacha20.decrypt_poly1305(inputData, filename, password);
                    case "RSA":
                        return App.rsa.decrypt(inputData);
                    default:
                        throw new AssertionError("Unexpected algorithm: " + curAlg.algName);
                }
                // return null; // Placeholder (ensure all cases return)
            }
        };

        // Handle task completion
        decryptionTask.setOnSucceeded(workerStateEvent -> {
            progressDialog.close();
            byte[] encryptedData = decryptionTask.getValue();
            if (encryptedData != null) {
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputTB.getText()))) {
                    bos.write(encryptedData);
                    bos.flush();
                    Logger.showInfo("File saved succesfully!", "File saved", "File saved successfully!");
                    resetWindow();
                } catch (IOException e) {
                    Logger.showError("An error occurred while writing the output file!", "Output File Error", "Error: " + e.getMessage());
                }
            }
            
        });

        decryptionTask.setOnFailed(workerStateEvent -> {
            progressDialog.close();
            Logger.showError("An error occurred during decryption!: "+ decryptionTask.getException().getMessage(), "Decryption Error", "Error: " + decryptionTask.getException().getMessage());
            
        });

        decryptionTask.setOnCancelled(workerStateEvent -> {
            progressDialog.close();
            Logger.showWarning("Decryption was cancelled!", "Decryption Cancelled", "Decryption was cancelled!");
        });

        // Show dialog and start task
        progressDialog.show();
        new Thread(decryptionTask).start();

    }
}
