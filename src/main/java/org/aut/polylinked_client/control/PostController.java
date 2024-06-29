package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.aut.polylinked_client.SceneManager;

public class PostController {
    private final static String fileId = "post"; // post.css and post.fxml

    @FXML
    private GNAvatarView avatar;

    @FXML
    private Hyperlink commentsLink;

    @FXML
    private Label dateLabel;

    @FXML
    private Button followButton;

    @FXML
    private Hyperlink likesLink;

    @FXML
    private Hyperlink nameLink;

    @FXML
    private Hyperlink repostLink;

    @FXML
    private GridPane root;

    @FXML
    private Text textArea;

    @FXML
    private VBox vBox;

    @FXML
    void initialize() {
        SceneManager.activateTheme(root, fileId);

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });
    }

    @FXML
    void commentPressed(ActionEvent event) {

    }

    @FXML
    void likePressed(ActionEvent event) {

    }

    @FXML
    void repostPressed(ActionEvent event) {

    }

    @FXML
    void sendPressed(ActionEvent event) {

    }

}
