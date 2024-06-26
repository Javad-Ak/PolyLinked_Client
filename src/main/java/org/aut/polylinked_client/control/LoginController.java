package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.utils.DataAccess;

public class LoginController {

    @FXML
    private JFXToggleButton themeToggle;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label messageText;

    @FXML
    private JFXTextField emailText;

    @FXML
    private JFXTextField passwordText;

    @FXML
    public void initialize() {
        String theme = DataAccess.getTheme();
        themeToggle.setSelected(!theme.equalsIgnoreCase("light"));

        themeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String val = newValue ? "dark" : "light";
            PolyLinked.changeTheme(DataAccess.Theme.valueOf(val), SceneManager.SceneLevel.LOGIN.id);
        });
    }

    @FXML
    void forgotPasswordPressed(ActionEvent event) {

    }

    @FXML
    void loginPressed(ActionEvent event) {

    }

    @FXML
    void signupPressed(ActionEvent event) {

    }
}
