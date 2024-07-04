package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.*;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.aut.polylinked_client.view.MediaWrapper;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContentController {
    private final static String fileId = "content"; // content.css file reference
    private final static Path defaultFace = Path.of("src/main/resources/org/aut/polylinked_client/images/face.jpg");

    private GridPane backTo;

    @FXML
    private GNAvatarView avatar;

    @FXML
    private Hyperlink commentsLink;

    @FXML
    private Label dateLabel;

    @FXML
    private Button followButton;

    @FXML
    private Button likeButton;

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

    public <T extends MediaLinked> void setData(T content, User user) {
        if (content instanceof Post post)
            setData(post, user);
        else if (content instanceof Comment comment)
            setData(comment, user);
        else if (content instanceof Message message)
            setData(message, user);
    }

    // fill data into fxml using fxmlLoader.getController
    private void setData(Post post, User user) {
        if (post == null || user == null) return;
        root.setUserData(post);

        // fill data into fxml
        nameLink.setText(user.getFirstName() + " " + user.getLastName());
        likesLink.setText(post.getLikesCount() + " likes");
        commentsLink.setText(post.getCommentsCount() + " comments");
        textArea.setText(post.getText());

        Date date = new Date(post.getDate());
        dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));

        setUpLike(post);
        setUpFollow(post);

        if (post.isReposted()) {
            repostLink.setText(post.getRepostFrom());
        } else {
            VBox vBox = (VBox) nameLink.getParent();
            vBox.getChildren().clear();
            vBox.getChildren().add(nameLink);
        }

        File file = DataAccess.getFile(user.getUserId(), user.getMediaURL());
        File media = DataAccess.getFile(post.getPostId(), post.getMediaURL());
        setUpMedias(file, media);
    }

    private void setData(Comment comment, User user) {
        if (comment == null || user == null) return;
        deleteRows();

        nameLink.setText(user.getFirstName() + " " + user.getLastName());
        textArea.setText(comment.getText());

        Date date = comment.getCreateDate();
        dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));

        File file = DataAccess.getFile(user.getUserId(), user.getMediaURL());
        File media = DataAccess.getFile(comment.getPostId(), comment.getMediaURL());
        setUpMedias(file, media);
    }

    private void setData(Message message, User user) {
        if (message == null || user == null) return;
        deleteRows();
        root.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == 0);

        nameLink.setText(user.getFirstName() + " " + user.getLastName());
        textArea.setText(message.getText());

        Date date = new Date(message.getDate());
        dateLabel.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));

        File file = DataAccess.getFile(user.getUserId(), user.getMediaURL());
        File media = DataAccess.getFile(message.getPostId(), message.getMediaURL());
        setUpMedias(file, media);
    }

    @FXML
    void commentPressed(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(PolyLinked.class.getResource("fxmls/comments.fxml"));
            Parent parent = loader.load();

            CommentsController controller = loader.getController();
            controller.setData(root, (Post) root.getUserData());
            Scene scene = new Scene(parent);
            SceneManager.setScene(scene);

//            controller.isPageActive().addListener((observable, oldValue, newValue) -> {
//                if (!newValue) SceneManager.setScene(SceneL);
//            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @FXML
    void repostPressed(ActionEvent event) {

    }

    @FXML
    void sendPressed(ActionEvent event) {

    }

    void setUpFollow(Post post) {
        if (post.getUserId().equals(DataAccess.getUserId())) {
            followButton.setDisable(true);
            followButton.setVisible(false);
            return;
        }

        JSONObject followedHeader = RequestBuilder.buildHeadRequest("follows/" + post.getUserId(), JsonHandler.createJson("Authorization", DataAccess.getJWT()));
        if (followedHeader != null && followedHeader.getString("Exists") != null && followedHeader.getString("Exists").equalsIgnoreCase("true")) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("  Follow  ");
        }

        followButton.setOnAction((ActionEvent event) -> {
            String method;
            String newText = followButton.getText();
            if (newText.contains("Follow")) {
                method = "POST";
                newText = "Unfollow";
            } else {
                method = "DELETE";
                newText = "  Follow  ";
            }

            String finalNewText = newText;
            new Thread(() -> {
                try {
                    RequestBuilder.sendJsonRequest(
                            method, "follows",
                            JsonHandler.createJson("Authorization", DataAccess.getJWT()),
                            new Follow(DataAccess.getUserId(), post.getUserId()).toJson());

                    Platform.runLater(() -> {
                        followButton.setText(finalNewText);
                    });
                } catch (NotAcceptableException e) {
                    Platform.runLater(() -> {
                        SceneManager.showNotification("Failure", "Request failed.", 3);
                    });
                } catch (UnauthorizedException e) {
                    Platform.runLater(() -> {
                        SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                        SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                    });
                }
            }).start();
        });
    }


    void setUpLike(Post post) {
        FontIcon likeIcon = new FontIcon("mdi-thumb-up-outline");
        likeIcon.setId("icon");
        FontIcon likedIcon = new FontIcon("mdi-thumb-up");
        likedIcon.setId("icon");

        JSONObject likedHeader = RequestBuilder.buildHeadRequest("likes/" + post.getPostId(), JsonHandler.createJson("Authorization", DataAccess.getJWT()));
        if (likedHeader != null && likedHeader.getString("Exists") != null && likedHeader.getString("Exists").equalsIgnoreCase("true")) {
            likeButton.setGraphic(likedIcon);
        } else {
            likeButton.setGraphic(likeIcon);
        }

        likeButton.setOnAction((ActionEvent event) -> {
            String method;
            FontIcon newIcon = (FontIcon) likeButton.getGraphic();
            int count;
            if (newIcon.getIconLiteral().equals(likeIcon.getIconLiteral())) {
                method = "POST";
                count = 1;
                newIcon = likedIcon;
            } else {
                method = "DELETE";
                count = -1;
                newIcon = likeIcon;
            }

            FontIcon finalNewIcon = newIcon;
            new Thread(() -> {
                try {
                    post.setLikesCount(post.getLikesCount() + count);
                    RequestBuilder.sendJsonRequest(
                            method, "likes",
                            JsonHandler.createJson("Authorization", DataAccess.getJWT()),
                            new Like(post.getPostId(), DataAccess.getUserId()).toJson());

                    Platform.runLater(() -> {
                        likesLink.setText(post.getLikesCount() + " likes");
                        likeButton.setGraphic(finalNewIcon);
                    });
                } catch (NotAcceptableException e) {
                    Platform.runLater(() -> {
                        post.setLikesCount(post.getLikesCount() - count);
                        SceneManager.showNotification("Failure", "Request failed.", 3);
                    });
                } catch (UnauthorizedException e) {
                    Platform.runLater(() -> {
                        SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                        SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                    });
                }
            }).start();
        });
    }

    private void deleteRows() {
        root.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= 2 &&
                !(node.getId() != null && node.getId().equals("date")));

        VBox vBox = (VBox) nameLink.getParent();
        vBox.getChildren().clear();
        vBox.getChildren().add(nameLink);
        followButton.setVisible(false);
        followButton.setDisable(true);
    }

    private void setUpMedias(File file, File media) {
        DataAccess.FileType type = DataAccess.getFileType(file);
        if (file != null && type == DataAccess.FileType.IMAGE)
            avatar.setImage(new Image(file.toURI().toString()));
        else
            avatar.setImage(new Image(defaultFace.toUri().toString()));

        if (media != null && media.length() > 0) {
            MediaWrapper viewer = MediaWrapper.getMediaViewer(media, 0.45);
            mediaBox.getChildren().clear();
            mediaBox.getChildren().add(viewer);
        } else {
            mediaBox.getChildren().clear();
        }
    }
}
