package org.aut.polylinked_client.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.view.UserCell;

import java.util.List;

public class UserListController {
    ListView<User> listView;
    ObservableList<User> notifications = FXCollections.observableArrayList();

    public UserListController(List<User> users) {
        listView = new ListView<>();

        notifications.addAll(users);
        listView.setItems(notifications);
        listView.setCellFactory(listView -> new UserCell());
    }

    public ListView<User> getRoot() {
        return listView;
    }
}
