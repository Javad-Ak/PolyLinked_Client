package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostController {
    private final static String fileId = "post"; // post.css file reference

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

    // fill data into fxml using fxmlLoader.getController
    public void setData(Post post, User user) {
        nameLink.setText(user.getFirstName() + " " + user.getLastName());
        likesLink.setText(post.getLikesCount() + " likes");
        commentsLink.setText(post.getCommentsCount() + " comments");
        textArea.setText(post.getText());

        Date date = new Date(post.getDate());
        dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));

        if (post.isReposted()) {
            repostLink.setText(post.getRepostFrom());
        } else {
            VBox vBox = (VBox) nameLink.getParent();
            vBox.getChildren().clear();
            vBox.getChildren().add(nameLink);
        }

        try {
            RequestBuilder.FileType type =
                    RequestBuilder.fileTypeFromHeadRequest(user.getMediaURL(), new JSONObject());
            if (type == RequestBuilder.FileType.IMAGE)
                avatar.setImage(new Image(user.getMediaURL()));
        } catch (UnauthorizedException e) {
            SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
        }
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
