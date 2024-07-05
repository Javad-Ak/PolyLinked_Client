package org.aut.polylinked_client.view;

import io.github.gleidson28.AvatarType;
import io.github.gleidson28.GNAvatarView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import java.io.File;
import java.util.Objects;

public class UserCell extends ListCell<User> {
    HBox root;
    GNAvatarView avatar;
    Label name;

    public UserCell() {
        root = new HBox();
        name = new Label("Name");

        avatar = new GNAvatarView();
        avatar.setType(AvatarType.CIRCLE);
        avatar.setImage(new Image(Objects.requireNonNull(PolyLinked.class.getResourceAsStream("images/avatar.png"))));
        avatar.setPrefWidth(45);
        avatar.setPrefHeight(45);

        root.setPrefWidth(HBox.USE_COMPUTED_SIZE);
        root.setPrefHeight(HBox.USE_COMPUTED_SIZE);
        root.setSpacing(10);
        root.setBackground(Background.EMPTY);
        root.setAlignment(Pos.CENTER_LEFT);
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
