package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.aut.polylinked_client.PolyLinked;

public class SignupController {

    @FXML
    private JFXTextField LastNameText;

    @FXML
    private JFXTextField confirmPasswordText;

    @FXML
    private JFXTextField emailText;

    @FXML
    private JFXTextField firstNameText;

    @FXML
    private JFXTextField passwordText;

    @FXML
    void loginClicked(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.login);
    }

    @FXML
    void signupClicked(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.home);
    }

}
