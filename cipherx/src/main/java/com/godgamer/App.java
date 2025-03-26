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
    private enum ext {
        png,
        jpg
    }
    
    private static Image getImage(String img, String ext) {
        return new Image(App.class.getResource("Images/" + img + "." + ext).toString());
    }
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadFXML("MainScene");
        scene = new Scene(root);
        // scene = new Scene(loadFXML("primary"), 640, 480);
        Image icon = getImage("icon", ext.jpg.toString());
        stage.getIcons().add(icon);
        stage.setTitle("CipherX");
        // stage.setResizable(false);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    // static void setRoot(String fxml) throws IOException {
    //     scene.setRoot(loadFXML(fxml));
    // }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Scenes/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}