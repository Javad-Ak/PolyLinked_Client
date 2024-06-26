package org.aut.polylinked_client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.aut.polylinked_client.PolyLinked;

public class LoginController {
    @FXML
    void loginPressed(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.HOME);
    }

    @FXML
    void signupPressed(ActionEvent event) {
        PolyLinked.setScene(PolyLinked.SceneLevel.SIGNUP);
    }

}
