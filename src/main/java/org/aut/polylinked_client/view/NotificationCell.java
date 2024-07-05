package org.aut.polylinked_client.view;

import io.github.gleidson28.GNAvatarView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import java.io.File;

public class NotificationCell extends ListCell<User> {
    BorderPane root;
    GNAvatarView avatar;
    Button button;
    Label label;

    public NotificationCell() {
        root = new BorderPane();
        avatar = new GNAvatarView();
        button = new Button("Accept Connection");
        label = new Label("Notification");

        HBox leftBox = new HBox();
        leftBox.getChildren().addAll(avatar, label);

        HBox rightBox = new HBox();
        rightBox.getChildren().addAll(button);

        root.setCenter(leftBox);
        root.setRight(rightBox);
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            label.setText(item.getFirstName() + " " + item.getLastName() + " requests to connect.");
            File file = DataAccess.getFile(item.getUserId(), item.getMediaURL());
            avatar.setImage(new Image(file.toURI().toString()));

            button.setOnAction((e) -> {

            });

            setGraphic(root);
        }
    }
}
