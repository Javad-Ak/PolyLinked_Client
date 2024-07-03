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
    private String id;
    private Post post;
    private User user;

    public PostCell(Post post, User user) {
        id = post.getPostId();
        this.post = post;
        this.user = user;
    }

    //    ############################################################

    private FXMLLoader fxmlLoader;

    private Parent root;

    public PostCell() {
        if (fxmlLoader == null) {
            try {
                fxmlLoader = new FXMLLoader(PolyLinked.class.getResource("fxmls/post.fxml"));
                root = fxmlLoader.load();
            } catch (IOException e) {
                System.err.println("failed to load post fxml");
                System.exit(1);
            }
        }
    }

    @Override
    protected void updateItem(PostCell item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            PostController controller = fxmlLoader.getController();
            controller.setData(item.post, item.user);
            setGraphic(root);
        }
    }
}
