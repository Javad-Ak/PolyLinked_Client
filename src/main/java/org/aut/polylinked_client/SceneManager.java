package org.aut.polylinked_client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.aut.polylinked_client.utils.DataAccess;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static  Stage stage;
    private static final StringProperty theme = new SimpleStringProperty();

    public SceneManager(Stage primaryStage) {
        stage = primaryStage;
        theme.set(DataAccess.getTheme());
    }

    public static void activateTheme(String cssID) { // on current scene
        activateTheme(stage.getScene().getRoot(), cssID);
    }

    public static void activateTheme(Parent root, String cssID) {
        URL css = PolyLinked.class.getResource("styles/" + theme.getValue() + "/" + cssID + ".css");
        if (css != null) {
            root.getStylesheets().set(0, css.toExternalForm());
        }
    }

    public static StringProperty getThemeProperty() {
        return theme;
    }

    public static void setThemeProperty(SceneManager.Theme newTheme) {
        theme.set(newTheme.value);
        DataAccess.setTheme(newTheme);
    }

    public void setScene() {
        if (DataAccess.getJWT().equals("null"))
            setScene(SceneLevel.LOGIN);
        else
            setScene(SceneLevel.MAIN);
    }

    public void setScene(SceneLevel sceneLevel) {
        Scene scene = sceneLevel.getScene();
        if (stage == null) {
            System.out.println("Primary stage not set");
            System.exit(1);
        }

        makeResponsive(scene, sceneLevel.id);
        stage.setScene(scene);
    }

    private void makeResponsive(Scene scene, String themeId) {
        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

//        scene.getStylesheets().add("-fx-font-size: " + (int) (13 * width * height / 1920 / 1080) + ";");
//        scene.getStylesheets().add("-fx-pref-width: " + (int) (800 * width / 1920) + ";");
//        scene.getStylesheets().add("-fx-pref-height: " + (int) (600 * height / 1080) + ";");

        String theme = DataAccess.getTheme();
        URL css = PolyLinked.class.getResource("styles/" + theme + "/" + themeId + ".css");
        Parent parent = scene.getRoot();
        if (css != null) {
            parent.getStylesheets().clear();
            parent.getStylesheets().add(css.toExternalForm());
        }
    }

    public enum SceneLevel {
        LOGIN("login"),
        SIGNUP("signup"),
        MAIN("main");

        private final URL fxmlURL;
        public final String id;

        SceneLevel(String id) {
            this.id = id;
            fxmlURL = PolyLinked.class.getResource("fxmls/" + id + ".fxml");
        }

        Scene getScene() {
            try {
                FXMLLoader loader = new FXMLLoader(fxmlURL);
                return new Scene(loader.load());
            } catch (IOException e) {
                System.err.println("Error loading fxml: " + fxmlURL);
                System.exit(1);
            }
            return null;
        }
    }

    public enum Theme {
        LIGHT("light"), DARK("dark");

        public final String value;

        Theme(String value) {
            this.value = value;
        }
    }
}
