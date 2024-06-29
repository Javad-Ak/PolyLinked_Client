package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.aut.polylinked_client.view.PostCell;

import java.util.TreeMap;

public class HomeController {

    private final static String fileId = "home"; // home.css

    private TreeMap<Post, User> postsData; // from server

    private final ObservableList<PostCell> observablePosts = FXCollections.observableArrayList(); // to listView

    @FXML
    private BorderPane root;

    @FXML
    private Label fileLabel;

    @FXML
    private ListView<PostCell> postListView; // <?> changed to <PostCell>

    @FXML
    private JFXTextArea postText;

    @FXML
    void initialize() {
        SceneManager.activateTheme(root, fileId);

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });

        try {
            postsData = RequestBuilder.mapFromGetRequest(Post.class, "newsfeed", JsonHandler.createJson("Authorization", DataAccess.getJWT()));
        } catch (UnauthorizedException e) {
            SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
        }

        if (postsData == null || postsData.isEmpty()) {
            root.setCenter(new Label("No posts found. Please try again later."));
        } else {
            postsData.forEach((post, user) -> observablePosts.add(new PostCell(post, user)));
            postListView.setItems(observablePosts);

            postListView.setCellFactory(listView -> new PostCell());
        }
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
