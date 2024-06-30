package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.SceneManager;

public class HomeController {
    private final static String fileId = "home"; // post.css and post.fxml

    @FXML
    private BorderPane root;

    @FXML
    private Label fileLabel;

    @FXML
    private ListView<?> postListView;

    @FXML
    private JFXTextArea postText;

    @FXML
    void initialize() {
        SceneManager.activateTheme(root, fileId);

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });
    }

    @FXML
    void audioPressed(ActionEvent event) {

    }

    @FXML
    void photoPressed(ActionEvent event) {

    }

    @FXML
    void postPressed(ActionEvent event) {

    }

    @FXML
    void videoPressed(ActionEvent event) {

    }

}
