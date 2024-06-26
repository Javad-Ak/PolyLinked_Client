package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoginController {

    @FXML
    private JFXToggleButton themeToggle;

    @FXML
    private Label messageText;

    @FXML
    private JFXTextField emailText;

    @FXML
    private JFXTextField passwordText;

    @FXML
    public void initialize() {
        String theme = DataAccess.getTheme();
        themeToggle.setSelected(theme.equalsIgnoreCase("dark"));

        themeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String val = newValue ? "DARK" : "LIGHT";
            PolyLinked.changeTheme(DataAccess.Theme.valueOf(val), SceneManager.SceneLevel.LOGIN.id);
        });
    }

    @FXML
    void forgotPasswordPressed(ActionEvent event) {
        messageText.setText("Email service not available");
    }

    @FXML
    void loginPressed(ActionEvent event) {
        try {
            JSONObject headers = new JSONObject();
            headers.put("Content-Type", "application/json");
            HttpURLConnection con = RequestBuilder.buildConnection("POST", "users/login",
                    headers, true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", emailText.getText());
            jsonObject.put("password", passwordText.getText());
            OutputStream outputStream = con.getOutputStream();
            JsonHandler.sendObject(outputStream, jsonObject);
            outputStream.close();

            if (con.getResponseCode() / 100 == 2) {
                InputStream inputStream = con.getInputStream();
                String JWT = JsonHandler.getObject(inputStream).getString("Authorization");
                DataAccess.setJWT(JWT);
                PolyLinked.setScene(SceneManager.SceneLevel.HOME);
            } else if (con.getResponseCode() == 401) {
                throw new UnauthorizedException("Invalid email or password");
            } else {
                throw new NotAcceptableException("Unknown");
            }
        } catch (IOException | NotAcceptableException e) {
            messageText.setText("Something went wrong!");
        } catch (UnauthorizedException e) {
            messageText.setText(e.getMessage());
        }
    }

    @FXML
    void signupPressed(ActionEvent event) {
        PolyLinked.setScene(SceneManager.SceneLevel.SIGNUP);
    }
}
