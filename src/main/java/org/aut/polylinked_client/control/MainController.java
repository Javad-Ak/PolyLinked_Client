package org.aut.polylinked_client.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

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
    }

    @FXML
    void aboutAppPressed(ActionEvent event) {

    }

    @FXML
    void deleteAccountPressed(ActionEvent event) {

    }

    @FXML
    void logOutPressed(ActionEvent event) {

    }

    @FXML
    void switchThemePressed(ActionEvent event) {

    }
}
