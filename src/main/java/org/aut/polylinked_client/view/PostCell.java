package org.aut.polylinked_client.view;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.control.PostController;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;

import java.io.IOException;

public class PostCell extends ListCell<Post> {
    private Parent root;

    public PostCell(Post post, User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(PolyLinked.class.getResource("fxmls/post.fxml"));
        try {
            root = fxmlLoader.load();
            PostController controller = fxmlLoader.getController();
            controller.setData(post, user);
        } catch (IOException e) {
            System.err.println("failed to load post fxml");
            System.exit(1);
        }
    }

    @Override
    protected void updateItem(Post item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(root);
        }
    }
}
