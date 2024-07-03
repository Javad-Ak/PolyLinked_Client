package org.aut.polylinked_client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.SceneManager;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.FontIcon;



public class ProfileController {
    private final static String fileId = "profile";

    @FXML
    private BorderPane rootBp;

    @FXML
    void initialize() {
        FontIcon icon = new FontIcon("mdi-dots-horizontal-circle-outline");
        SceneManager.activateTheme(rootBp, fileId);

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(rootBp, fileId);
        });


    }

    @FXML
    void followersBtnPressed(ActionEvent event) {

    }
    @FXML
    void followingBtnPressed(ActionEvent event) {}

    @FXML
    void editProfileBtnPressed(ActionEvent event) {}


    @FXML
    void followBtnPressed(ActionEvent event) {
    }

    @FXML
    void blockBtnPressed(ActionEvent event) {}

    @FXML
    void connectBtnPressed(ActionEvent event) {}

}
