package org.aut.polylinked_client.view;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.control.PostController;
import org.aut.polylinked_client.model.Post;
import org.aut.polylinked_client.model.User;

import java.io.IOException;

// this class serves both as model and view
public class PostCell extends ListCell<PostCell> {
    private Post post;
    private User user;

    public PostCell(Post post, User user) {
        this.post = post;
        this.user = user;
    }

//    ########################################

    private Parent root;
    private PostController controller;

    public PostCell() {
        FXMLLoader fxmlLoader = new FXMLLoader(PolyLinked.class.getResource("fxmls/post.fxml"));
        try {
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            System.err.println("failed to load post fxml");
            System.exit(1);
        }
    }

    @Override
    protected void updateItem(PostCell item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setData(item.post, item.user);
            setGraphic(root);
        }
    }
}
