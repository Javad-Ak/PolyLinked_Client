package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.Message;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.aut.polylinked_client.view.ContentCell;
import org.aut.polylinked_client.view.MapListView;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class MessagingController {
    private final static String fileId = "home"; // home.css

    private static MapListView<Post> mapListView;

    private File pickedFile;

    private User user;

    @FXML
    private Button fileButton;

    @FXML
    private VBox mediaBox;

    @FXML
    private JFXTextArea messageText;

    @FXML
    private BorderPane root;

    @FXML
    private BorderPane pane;

    @FXML
    void initialize() {
        // theme observation
        SceneManager.activateTheme(root, "home");
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, "home");
        });

        fileButton.setOnAction(e -> {
            fileButton.setText("");
            fileButton.setVisible(false);
            pickedFile = null;
            mediaBox.getChildren().clear();
        });
        fileButton.fire();

        new Thread(() -> {
            try {
                JSONObject headers = JsonHandler.createJson("Authorization", DataAccess.getJWT());
                List<User> connections = RequestBuilder.arrayFromGetRequest(User.class, "connections/" + DataAccess.getUserId(), headers);

                if (connections.isEmpty()) {
                    Platform.runLater(() -> {
                        root.setCenter(new Label("No connections found"));
                    });
                } else {
                    UserListController controller = new UserListController(connections);
                    Platform.runLater(() -> {
                        root.setLeft(controller.getListView());
                    });

                    controller.getListView().getSelectionModel().selectedItemProperty().addListener((observable, oldUser, newUser) -> {
                        new Thread(() -> {
                            user = newUser;

                            try {
                                List<Message> messages = RequestBuilder.arrayFromGetRequest(Message.class,
                                        "messages/" + DataAccess.getUserId() + user.getUserId(), headers);
                                TreeMap<Message, User> messageData = new TreeMap<>();
                            } catch (UnauthorizedException e) {
                                Platform.runLater(() -> {
                                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                                });
                            }
                        }).start();
                    });
                }
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            }
        }).start();
    }

    @FXML
    void audioPressed(ActionEvent event) {

    }

    @FXML
    void photoPressed(ActionEvent event) {

    }

    @FXML
    void sendPressed(ActionEvent event) {

    }

    @FXML
    void videoPressed(ActionEvent event) {

    }

}