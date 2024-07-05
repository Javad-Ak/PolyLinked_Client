package org.aut.polylinked_client.view;

import io.github.gleidson28.GNAvatarView;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import java.io.File;
import java.util.Objects;

public class UserCell extends ListCell<User> {
    HBox root;
    GNAvatarView avatar;
    Hyperlink name;

    public UserCell() {
        root = new HBox();
        name = new Hyperlink("Name");

        avatar = new GNAvatarView();
        avatar.setImage(new Image(Objects.requireNonNull(PolyLinked.class.getResourceAsStream("images/avatar.png"))));
        avatar.setPrefWidth(50);
        avatar.setPrefHeight(50);

        root.setStyle("-fx-pref-width: computed-size;");
        root.setStyle("-fx-pref-height: computed-size;");
        root.getChildren().addAll(avatar, name);
    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            name.setText(item.getFirstName() + " " + item.getLastName());
            File file = DataAccess.getFile(item.getUserId(), item.getMediaURL());
            if (file != null) avatar.setImage(new Image(file.toURI().toString()));

            setGraphic(root);
        }
    }
}
