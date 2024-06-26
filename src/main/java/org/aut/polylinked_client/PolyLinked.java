package org.aut.polylinked_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * PolyLinked_Client is a LinkedIn clone Application. (in pair with PolyLinked_Server)
 *
 * @author AliReza AthariFard
 * @author MohammadJavd Akbari
 * @version 1.0
 */
public class PolyLinked extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("PolyLinked");
        setScene(SceneLevel.LOGIN);
        stage.show();
    }

    public static void launchApp(String[] args) {
        launch(args);
    }

    public static void setScene(SceneLevel sceneLevel) {
        Scene scene = sceneLevel.getScene();
        if (primaryStage == null || scene == null) return;

        primaryStage.setScene(scene);
    }

    public enum SceneLevel {
        LOGIN("fxmls/login.fxml"),
        SIGNUP("fxmls/signup.fxml"),
        HOME("fxmls/home.fxml");

        private final URL fxmlURL;

        SceneLevel(String path) {
            fxmlURL = Launcher.class.getResource(path);
        }

        Scene getScene() {
            try {
                FXMLLoader loader = new FXMLLoader(fxmlURL);
                return new Scene(loader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}