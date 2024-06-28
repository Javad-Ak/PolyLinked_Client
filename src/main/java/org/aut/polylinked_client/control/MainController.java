package org.aut.polylinked_client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.utils.DataAccess;

import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ToggleButton homeToggle;

    @FXML
    private ToggleButton messagingToggle;

    @FXML
    private ToggleButton notifToggle;

    @FXML
    private ToggleButton profileToggle;

    @FXML
    private ToggleButton searchToggle;

    @FXML
    private ToggleGroup tabs;

    @FXML
    public void initialize() {
        homeToggle.setSelected(true);
        tabs.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) tabs.selectToggle(oldValue);
        });

        // theme observation
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(SceneManager.SceneLevel.LOGIN.id);
        });
    }

    @FXML
    void aboutAppPressed(ActionEvent event) {
        String message = """
                PolyLinked is a LinkedIn clone desktop Application developed in javafx.\
                                
                This Application was the project of 2024 AP course at @AUT-CE.)\
                                
                Authors: Alireza Atharifard, MohammadJavad Akbari""";

        Dialog<ButtonType> dialog = createDialogue("About PolyLinked", message, ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    void deleteAccountPressed(ActionEvent event) {
//        TODO
        logOutPressed(event);
    }

    @FXML
    void logOutPressed(ActionEvent event) {
        Dialog<ButtonType> dialog = createDialogue("Confirm", "Are you sure you want to log out?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataAccess.setUserId("none");
            DataAccess.setJWT("none");
            SceneManager.setScene(SceneManager.SceneLevel.SIGNUP);
        }
    }

    @FXML
    void switchThemePressed(ActionEvent event) {
        if (DataAccess.getTheme().equals("light"))
            SceneManager.setThemeProperty(SceneManager.Theme.DARK);
        else
            SceneManager.setThemeProperty(SceneManager.Theme.LIGHT);
    }

    private static Dialog<ButtonType> createDialogue(String title, String message, ButtonType... buttonTypes) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypes);

        dialog.getDialogPane().getChildren().forEach(n -> n.setStyle("-fx-text-fill: black;"));
        if (DataAccess.getTheme().equals("light"))
            dialog.getDialogPane().setStyle("-fx-background-color: #e8f1fc;");
        else
            dialog.getDialogPane().setStyle("-fx-background-color: #b9b9b9;");

        return dialog;
    }
}
