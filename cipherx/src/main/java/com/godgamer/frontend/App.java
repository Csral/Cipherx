package com.godgamer.frontend;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.godgamer.backend.Encryption.AES;
import com.godgamer.backend.Encryption.ChaCha20;
import com.godgamer.backend.Encryption.ECC;
import com.godgamer.backend.Encryption.RSA;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;
    public static Stage stage;
    public static LocalTime loginTime = LocalTime.now();
    private static String css;
    public static boolean isDarkMode = false;
    public static AES aes; // for encryption and decryption
    public static ChaCha20 chacha20; // for encryption and decryption
    public static RSA rsa; // for encryption and decryption
    public static ECC ecc;

    // Images
    public static Map<String, Image> images = new HashMap<>();

    public static enum IMAGE_EXTENSIONS {
        png,
        jpg,
        svg
    }
    
    // get image as JavaFX Image
    public static Image getImage(String img, String ext, boolean backgroundLoading) {
        return new Image(App.class.getResource("Images/" + img + "." + ext).toExternalForm(), backgroundLoading);
    }

    public static Image getImage(String img, String ext,double width, double height,  boolean backgroundLoading) {
        return new Image(App.class.getResource("Images/" + img + "." + ext).toExternalForm(),width, height, true, true ,backgroundLoading);
    }

    // get css address as string
    private static String getCSS(String css) {
        return App.class.getResource("Styles/" + css + ".css").toExternalForm();
    }

    @SuppressWarnings("static-access")
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage; // set the stage to the current stage
        // the main scene
        Parent root = loadFXML("MainScene");
        scene = new Scene(root);

        // adding css stylesheet
        css = getCSS("MainSceneLight");
        // css = getCSS("testLight");
        scene.getStylesheets().add(css);

        // setting up the stage prerequisites
        Image icon = getImage("CypherX_Logo", IMAGE_EXTENSIONS.jpg.toString(), false); // for app icon
        stage.getIcons().add(icon);
        stage.setTitle("CipherX");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setResizable(false); // disable resizing the window
        stage.setScene(scene);

        // setting up the stage close request
        // this is where the app closes and performs actions while closing the app
        stage.setOnCloseRequest(e -> {
            onAppClosing(); // perform actions while closing the app
        });

        // display the stage
        stage.show();
    }

    // perform actions while closing the app
    private void onAppClosing()
    {
        // save logs to file
        Logger.printMessage("Application is closing.");
        Logger.saveLogs();
    }

    // changes the css file of the scene
    static void changeCSS(String css) {
        scene.getStylesheets().remove(App.css);
        scene.getStylesheets().add(getCSS(css));
        App.css = getCSS(css);
    }

    // to change scenes
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        changeCSS(isDarkMode ? "EncDecDark" : "EncDecLight");
    }

    @SuppressWarnings("CallToPrintStackTrace")
    static void setRoot(String fxml, String darkCSS, String lightCSS) throws IOException {
        try {
            Parent root = loadFXML(fxml);
            scene.setRoot(root);
            changeCSS(isDarkMode ? darkCSS : lightCSS);
            Logger.printMessage("Successfully switched to: " + fxml);
        } catch (IOException e) {
            Logger.printMessage("Failed to load FXML: " + fxml);
            e.printStackTrace();
        }
    }    

    // load new scene's fxml
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Scenes/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // load all images
        images.put("show pass", getImage("show pass", App.IMAGE_EXTENSIONS.png.toString(), 20d, 20d, true));
        images.put("hide pass", getImage("hide pass", App.IMAGE_EXTENSIONS.png.toString(), 20d, 20d, true));
        images.put("encrypt", getImage("encryption", App.IMAGE_EXTENSIONS.png.toString(), 88d, 58d, true));
        images.put("decrypt", getImage("Decryption", App.IMAGE_EXTENSIONS.png.toString(), 88d, 58d, true));
        images.put("obfuscate", getImage("Obfuscation", App.IMAGE_EXTENSIONS.png.toString(), 88d, 58d, true));
        images.put("cryptography", getImage("Cryptography", App.IMAGE_EXTENSIONS.png.toString(), 88d, 58d, true));
        images.put("steganography", getImage("Steganography", App.IMAGE_EXTENSIONS.png.toString(), 88d, 58d, true));
        images.put("startScreenLight", getImage("CypherX_start_image_light", App.IMAGE_EXTENSIONS.jpg.toString(), 200d, 150d, true));
        images.put("startScreenDark", getImage("CypherX_start_image", App.IMAGE_EXTENSIONS.jpg.toString(), 200d, 150d, true));

        // load AES class
        try { 
            aes = new AES();
            chacha20 = new ChaCha20();
            rsa = new RSA();
            ecc = new ECC();
            Logger.printMessage("Successfully Loaded algorithms");
        } catch (Exception e) {
            Logger.printMessage("Failed to load algorithms: " + e.getMessage());
        }

        // load the app
        launch(args);
    }

}