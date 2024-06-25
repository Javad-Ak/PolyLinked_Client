package org.aut.polylinked_client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PolyLinked extends Application {
    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        pane.setPrefSize(800, 600);
        Scene scene = new Scene(pane);
        stage.setTitle("PolyLinked");
        stage.setScene(scene);
        stage.show();
    }

    public static void launchApp(String[] args) {
        launch(args);
    }
}