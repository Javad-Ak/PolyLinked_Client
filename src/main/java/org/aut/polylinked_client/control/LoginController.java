package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.aut.polylinked_client.PolyLinked;

public class LoginController {
    @FXML
    private JFXTextField emailText;

    @FXML
    private JFXTextField passwordText;

    @FXML
    void forgotPasswordClicked(ActionEvent event) {

    }

    @FXML
    void loginClicked(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.home);
    }

    @FXML
    void signupClicked(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.signup);
    }

}

