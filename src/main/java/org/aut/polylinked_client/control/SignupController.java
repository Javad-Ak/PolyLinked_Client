package org.aut.polylinked_client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;

public class SignupController {
    @FXML
    void loginPressed(ActionEvent event) {
        PolyLinked.setScene(SceneManager.SceneLevel.LOGIN);
    }

    @FXML
    void signupPressed(ActionEvent event) {
        PolyLinked.setScene(SceneManager.SceneLevel.MAIN);
    }
}