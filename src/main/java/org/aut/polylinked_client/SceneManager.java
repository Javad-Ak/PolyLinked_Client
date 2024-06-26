package org.aut.polylinked_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.aut.polylinked_client.utils.DataAccess;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void changeTheme(DataAccess.Theme theme, String themeId) {
        DataAccess.setTheme(theme);
        URL css = PolyLinked.class.getResource("styles/" + theme.value + "/" + themeId + ".css");
        if (css != null && !stage.getScene().getStylesheets().isEmpty()) {
            stage.getScene().getStylesheets().set(0, css.toExternalForm());
        }
    }

    public void setScene() {
        if (DataAccess.getJWT().equals("null"))
            setScene(SceneLevel.LOGIN);
        else
            setScene(SceneLevel.HOME);
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
        String theme = DataAccess.getTheme();

        URL css = PolyLinked.class.getResource("styles/" + theme + "/" + themeId + ".css");
        Parent parent = scene.getRoot();

        if (css != null) {
            parent.getStylesheets().clear();
            scene.getStylesheets().add(css.toExternalForm());
        }

        parent.setStyle("-fx-font-size: " + (int) (13 * width * height / 1920 / 1080) + ";");
        parent.setStyle("-fx-pref-width: " + (int) (800 * width / 1920) + ";");
        parent.setStyle("-fx-pref-height: " + (int) (600 * height / 1080) + ";");
    }

    public enum SceneLevel {
        LOGIN("login"),
        SIGNUP("signup"),
        HOME("home");

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
}
