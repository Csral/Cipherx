package com.godgamer;

import java.io.IOException;

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

    private static Scene scene;
    private static String css;

    private static enum IMAGE_EXTENSIONS {
        png,
        jpg
    }
    
    // get image as JavaFX Image
    private static Image getImage(String img, String ext) {
        return new Image(App.class.getResource("Images/" + img + "." + ext).toExternalForm());
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
        css = getCSS("Application");
        scene.getStylesheets().add(css);

        // setting up the stage prerequisites
        Image icon = getImage("icon", IMAGE_EXTENSIONS.jpg.toString()); // for app icon
        stage.getIcons().add(icon);
        stage.setTitle("CipherX");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    // to change scenes
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // load new scene's fxml
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Scenes/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}