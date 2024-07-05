package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.CallInfo;
import org.aut.polylinked_client.model.Comment;
import org.aut.polylinked_client.model.Profile;
import org.aut.polylinked_client.model.User;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.view.ContentCell;
import org.aut.polylinked_client.view.MapListView;
import org.json.JSONObject;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class EditProfileController {
    private final static String fileId = "editProfile";
    private final BooleanProperty switched = new SimpleBooleanProperty(false);

    private User initialUser;
    private Profile initialProfile;
    private CallInfo initialCallInfo;

    @FXML
    private BorderPane root;

    @FXML
    private JFXTextField firstNameTF;

    @FXML
    private JFXTextField lastNameTF;

    @FXML
    private JFXTextField emailTF;

    @FXML
    private JFXTextField additionalNameTF;

    @FXML
    private JFXPasswordField passwordTF;

    @FXML
    private JFXPasswordField confirmPasswordTF;

    @FXML
    private JFXTextArea bioTA;

    @FXML
    private JFXComboBox <String> countryCB;

    @FXML
    private JFXComboBox <String> cityCB;

    @FXML
    private JFXComboBox <String> professionCB;

    @FXML
    private JFXComboBox <String> statusCB;

    @FXML
    private JFXTextField mobileNumberTF;

    @FXML
    private JFXTextField homeNumberTF;

    @FXML
    private JFXTextField workNumberTF;

    @FXML
    private JFXTextArea addressTA;

    @FXML
    private DatePicker birthdayDP;

    @FXML
    private JFXTextField socialMediaTF;

    @FXML
    private JFXComboBox<String> privacyCB;

    @FXML
    private JFXTextField instituteTF;

    @FXML
    private JFXTextField fieldTF;

    @FXML
    private DatePicker startDateDP;

    @FXML
    private DatePicker endDateDP;

    @FXML
    private JFXTextField gradeTF;

    @FXML
    private JFXTextArea activitiesTA;

    @FXML
    private JFXTextArea aboutTA;

    @FXML
    private JFXTextArea skill1TA;

    @FXML
    private JFXTextArea skill2TA;

    @FXML
    private JFXTextArea skill3TA;

    @FXML
    void initialize(){
        SceneManager.activateTheme(root, fileId);
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });
    }

    void setData (String userId){
        new Thread(() -> {
            try {
                JSONObject headers = JsonHandler.createJson("Authorization", DataAccess.getJWT());

                User userX = null;
                try {
                    userX = new User(RequestBuilder.jsonFromGetRequest("users/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }

                CallInfo callInfoX = null;
                try {
                    callInfoX = new CallInfo(RequestBuilder.jsonFromGetRequest("users/callInfo/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }

                Profile profileX = null;
                try {
                    profileX = new Profile(RequestBuilder.jsonFromGetRequest("users/profiles/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }


                initialUser = userX;
                initialProfile = profileX;
                initialCallInfo = callInfoX;
                Platform.runLater(() -> {
                            if (initialUser != null) {
                                firstNameTF.setText(initialUser.getFirstName());
                                lastNameTF.setText(initialUser.getLastName());
                                additionalNameTF.setText(initialUser.getAdditionalName());
                                emailTF.setText(initialUser.getEmail());
                                passwordTF.setText(initialUser.getPassword());
                            }

                    if (initialUser != null && initialProfile != null) {
                        bioTA.setText(initialProfile.getBio());
                        countryCB.setValue(initialProfile.getCountry());
                        cityCB.setValue(initialProfile.getCity());
                        professionCB.setValue(initialProfile.getProfession());
                       statusCB.setValue(initialProfile.getStatus());
                    }

                    if (initialCallInfo != null) {
                       mobileNumberTF.setText(initialCallInfo.getMobileNumber());
                       homeNumberTF.setText(initialCallInfo.getHomeNumber());
                       workNumberTF.setText(initialCallInfo.getWorkNumber());
                       addressTA.setText(initialCallInfo.getAddress());
                       birthdayDP.setValue(LocalDate.ofInstant(Instant.ofEpochMilli(initialCallInfo.getBirthDay()) , ZoneId.systemDefault()));
                       socialMediaTF.setText(initialCallInfo.getSocialMedia());
                       privacyCB.setValue(initialCallInfo.getPrivacyPolitics());
                    }
                        });

            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            }
        }).start();
    }

    @FXML
    void cancelBtnPressed(ActionEvent event) {
        switched.set(true);
    }

    @FXML
    void saveBtnPressed(ActionEvent event) {
        if (initialUser == null ) {
            SceneManager.showNotification("Info", "Not Found!", 3);
            return;
        }

        if(personalChanged()) {
            if (firstNameTF.getText().isEmpty() || lastNameTF.getText().isEmpty() || (emailTF.getText().isEmpty()) || passwordTF.getText().isEmpty() || confirmPasswordTF.getText().isEmpty()) {
                SceneManager.showNotification("Info","Please fill all the fields.", 3);
                return;
            }

            if (!firstNameTF.getText().matches("(?i)^[a-z]{1,20}$")) {
                SceneManager.showNotification("Info","First name must be a maximum of 20 characters and consist of only letters.", 3);
                return;
            }
            if (!lastNameTF.getText().matches("(?i)^[a-z]{1,40}$")) {
                SceneManager.showNotification("Info","Last name must be a maximum of 40 characters and consist of only letters.", 3);
                return;
            }

            if (!additionalNameTF.getText().matches("(?i)^[a-z]{1,20}$") || additionalNameTF.getText().isEmpty()) {
                SceneManager.showNotification("Info","Additional name must be a maximum of 20 characters and consist of only letters.", 3);
                return;
            }

            if (!emailTF.getText().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
                SceneManager.showNotification("Info","Invalid email address.", 3);
                return;
            }


            if (!(passwordTF.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$"))) {
                SceneManager.showNotification("Info","The password must contain 8-20 characters, at least one letter and one digit.", 3);
                return;
            }

            if (!passwordTF.getText().equals(confirmPasswordTF.getText())) {
                SceneManager.showNotification("Info","Passwords don't match.", 3);
                return;
            }
        }



    }

    private boolean educationIsEmpty (){
        return instituteTF.getText().trim().isEmpty() && fieldTF.getText().trim().isEmpty() &&
                startDateDP.getValue() == null && endDateDP.getValue() == null && gradeTF.getText().trim().isEmpty() &&
                activitiesTA.getText().trim().isEmpty() && aboutTA.getText().trim().isEmpty() &&
                skill1TA.getText().trim().isEmpty() && skill2TA.getText().trim().isEmpty() && skill3TA.getText().trim().isEmpty();
    }

    private boolean personalChanged () {
        return !(firstNameTF.getText().equals(initialUser.getFirstName()) && lastNameTF.getText().equals(initialUser.getLastName()) &&
                additionalNameTF.getText().equals(initialUser.getAdditionalName()) && emailTF.getText().equals(initialUser.getEmail()) &&
                passwordTF.getText().equals(initialUser.getPassword()));
    }

    public BooleanProperty isSwitched() {
        return switched;
    }
}
