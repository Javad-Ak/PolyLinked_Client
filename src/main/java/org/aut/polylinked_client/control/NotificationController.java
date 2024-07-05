package org.aut.polylinked_client.control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.aut.polylinked_client.view.NotificationCell;

import java.util.List;

public class NotificationController {
    ListView<User> listView;
    ObservableList<User> notifications = FXCollections.observableArrayList();

    public NotificationController() {
        listView = new ListView<>();

        new Thread(() -> {
            try {
                List<User> users = RequestBuilder.arrayFromGetRequest(User.class,
                        "connections", JsonHandler.createJson("Authorization", DataAccess.getJWT()));

                Platform.runLater(() -> {
                    notifications.addAll(users.subList(0, 100));
                    listView.setItems(notifications);
                    listView.setCellFactory(listView -> new NotificationCell());
                });
            } catch (UnauthorizedException e) {
                SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
            }
        }).start();
    }

    public ListView<User> getRoot() {
        return listView;
    }
}
