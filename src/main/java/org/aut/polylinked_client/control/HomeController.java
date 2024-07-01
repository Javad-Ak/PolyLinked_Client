package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.aut.polylinked_client.view.PostCell;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

public class HomeController {

    private final static String fileId = "home"; // home.css

    private TreeMap<Post, User> postsData; // from server

    private final ArrayList<Post> sortedKeys = new ArrayList<>(); // sorted server keys

    private int index = 0; // current key index -> increments by 5 at each loading

    private final ObservableList<PostCell> observablePosts = FXCollections.observableArrayList();

    private File pickedFile;

    @FXML
    private Button fileButton;

    @FXML
    private JFXTextArea postText;

    @FXML
    private ListView<PostCell> postsListView; // <?> changed to <PostCell>

    @FXML
    private BorderPane root;


    @FXML
    void initialize() {
        SceneManager.activateTheme(root, fileId);

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });

        fileButton.setText("");
        fileButton.setVisible(false);
        fileButton.setOnAction(e -> {
            pickedFile = null;
            fileButton.setText("");
            fileButton.setVisible(false);
        });

        // All data from Data
        try {
            postsData = RequestBuilder.mapFromGetRequest(Post.class, "newsfeed", JsonHandler.createJson("Authorization", DataAccess.getJWT()));
        } catch (UnauthorizedException e) {
            SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
        }

        if (postsData == null || postsData.isEmpty()) {
            root.setCenter(new Label("No posts found. Please try again later."));
        } else {
            sortedKeys.addAll(postsData.keySet().stream().toList());
            sortedKeys.sort(Comparator.comparing(Post::getDate).reversed());

            postsListView.setItems(observablePosts); // cell data: bind listview to collection
            postsListView.setCellFactory(listView -> new PostCell()); // cell view

            loadBuffer(); // first loading
            activateLazyLoading(postsListView); // automatic further loading when scroller reaches the bottom
        }
    }

    @FXML
    void postPressed(ActionEvent event) {
        if (pickedFile == null && postText.getText().trim().isEmpty()) {
            SceneManager.showNotification("Info", "You cannot post empty content!", 3);
            return;
        }
        try {
            Post post = new Post(DataAccess.getUserId(), postText.getText().trim());
            RequestBuilder.sendMediaLinkedRequest("POST", "users/posts", JsonHandler.createJson("Authorization", DataAccess.getJWT()), post, pickedFile);
            SceneManager.showNotification("Success", "Your new Post Added.", 3);
        } catch (NotAcceptableException e) {
            SceneManager.showNotification("Failure", "Post Couldn't be added. Please try again later.", 3);
        } catch (UnauthorizedException e) {
            SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
        } finally {
            postText.setText("");
            pickedFile = null;
            fileButton.setText("");
            fileButton.setVisible(false);
        }
    }

    @FXML
    void audioPressed(ActionEvent event) {
        File file = SceneManager.showFileChooser(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac"));
        pickFile(file);
    }

    @FXML
    void videoPressed(ActionEvent event) {
        File file = SceneManager.showFileChooser(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.m4v", "*.flv"));
        pickFile(file);
    }

    @FXML
    void photoPressed(ActionEvent event) {
        File file = SceneManager.showFileChooser(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        pickFile(file);
    }

    private void pickFile(File file) {
        if (file == null) {
            fileButton.setText("");
            fileButton.setVisible(false);
        } else if (!file.isFile()) {
            SceneManager.showNotification("Failure", "File is corrupted.", 3);
        } else if (file.length() > 1000000000) {
            SceneManager.showNotification("Failure", "File is too large.", 3);
        } else {
            pickedFile = file;
            fileButton.setText(file.getName());
            fileButton.setVisible(true);
        }
    }

    private void loadBuffer() {
        int bufferSize = 10; // 5 posts are added when scroller reaches the bottom

        if (sortedKeys.isEmpty() || sortedKeys.size() - 1 < index) return;

        for (int i = index; i < index + bufferSize && i < sortedKeys.size(); i++)
            observablePosts.add(new PostCell(sortedKeys.get(i), postsData.get(sortedKeys.get(i))));

        index += bufferSize;
    }

    private void activateLazyLoading(ListView<?> listView) {
        listView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                ScrollBar scrollBar = (ScrollBar) listView.lookup(".scroll-bar:vertical");

                if (scrollBar != null && scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                    scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() > 0.85) {
                            loadBuffer();
                        }
                    });
                }
            }
        });
    }
}
