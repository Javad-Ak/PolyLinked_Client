package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.aut.polylinked_client.SceneManager;

public class ProfileController {
    private final static String fileId = "profile";

    @FXML
    private GNAvatarView avatar;

    @FXML
    private ImageView background;

    @FXML
    private Text bioLabel;

    @FXML
    private Text callInfoText;

    @FXML
    private Button editButton;

    @FXML
    private Text educationText;

    @FXML
    private Hyperlink followersLink;

    @FXML
    private Hyperlink followingsLink;

    @FXML
    private AnchorPane headerImagePane;

    @FXML
    private Label joinedDateLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private BorderPane root;

    @FXML
    private Text skillsText;

    @FXML
    void initialize() {
        // theme observation
        SceneManager.activateTheme(root, fileId);
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });

        background.fitWidthProperty().bind(SceneManager.getWidthProperty());
        background.fitHeightProperty().bind(SceneManager.getHeightProperty().multiply(0.25));
    }

    @FXML
    void connectPressed(ActionEvent event) {

    }

    @FXML
    void followPressed(ActionEvent event) {

    }
}
