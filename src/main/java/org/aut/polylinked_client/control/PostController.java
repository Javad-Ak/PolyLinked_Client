package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.view.MediaViewer;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostController {
    private final static String fileId = "post"; // post.css file reference
    private final static Path defaultFace = Path.of("src/main/resources/org/aut/polylinked_client/images/face.jpg");

    private Post post = null;

    private User user = null;

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
    private HBox mediaBox;

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
        if (post == null || user == null) return;
        this.post = post;
        this.user = user;

        // fill data into fxml
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

        File file = DataAccess.getFile(user.getUserId(), user.getMediaURL());
        DataAccess.FileType type = DataAccess.getFileType(file);
        if (file != null && type == DataAccess.FileType.IMAGE)
            avatar.setImage(new Image(file.toURI().toString()));
        else
            avatar.setImage(new Image(defaultFace.toUri().toString()));

        File media = DataAccess.getFile(post.getPostId(), post.getMediaURL());
        if (media != null && media.length() > 0) {
            MediaViewer viewer = MediaViewer.getMediaViewer(media, 0.45);
            mediaBox.getChildren().clear();
            mediaBox.getChildren().add(viewer);
        } else {
            mediaBox.getChildren().clear();
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

    public boolean isFilledWith(String postId) {
        if (post == null)
            return false;
        else
            return postId.equals(post.getPostId());
    }
}
