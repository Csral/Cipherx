package com.godgamer.frontend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private static String css;
    public static boolean isDarkMode = false;

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

    @Override
    public void start(Stage stage) throws IOException {
        // the main scene
        Parent root = loadFXML("MainScene");
        scene = new Scene(root);

        // adding css stylesheet
        css = getCSS("MainSceneLight");
        // css = getCSS("testLight");
        scene.getStylesheets().add(css);

        // setting up the stage prerequisites
        Image icon = getImage("logo3", IMAGE_EXTENSIONS.jpg.toString(), false); // for app icon
        stage.getIcons().add(icon);
        stage.setTitle("CipherX");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
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

    static void setRoot(String fxml, String darkCSS, String lightCSS) throws IOException {
        try {
            Parent root = loadFXML(fxml);
            scene.setRoot(root);
            changeCSS(isDarkMode ? darkCSS : lightCSS);
            System.out.println("Successfully switched to: " + fxml);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxml);
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
        images.put("logo", getImage("logo3", App.IMAGE_EXTENSIONS.jpg.toString(), 200d, 150d, true));
        launch();
    }

}