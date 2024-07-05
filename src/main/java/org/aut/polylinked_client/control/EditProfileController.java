package org.aut.polylinked_client.control;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.*;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.json.JSONObject;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
    private JFXComboBox<String> countryCB;

    @FXML
    private JFXTextField cityTF;

    @FXML
    private JFXComboBox<String> professionCB;

    @FXML
    private JFXComboBox<String> statusCB;

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
    private JFXTextArea skillTA;

    @FXML
    void initialize() {
        SceneManager.activateTheme(root, fileId);
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });

        countryCB.setItems(FXCollections.observableList(List.of("Iran", "USA", "UK", "Turkey", "Iraq")));
        countryCB.getSelectionModel().select(0);

        professionCB.setItems(FXCollections.observableList(List.of("DOCTOR", "TEACHER", "ENGINEER", "LAWYER", "MECHANIC")));
        professionCB.getSelectionModel().select(0);

        statusCB.setItems(FXCollections.observableList(List.of("RECRUITER", "SERVICE_PROVIDER", "JOB_SEARCHER")));
        statusCB.getSelectionModel().select(0);

        privacyCB.setItems(FXCollections.observableList(List.of("ONLY_ME", "MY_CONNECTIONS", "FURTHER_CONNECTIONS", "EVERYONE")));
        statusCB.getSelectionModel().select(0);
    }

    void setData(String userId) {
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
                        cityTF.setText(initialProfile.getCity());
                        professionCB.setValue(initialProfile.getProfession());
                        statusCB.setValue(initialProfile.getStatus());
                    }

                    if (initialCallInfo != null) {
                        mobileNumberTF.setText(initialCallInfo.getMobileNumber());
                        homeNumberTF.setText(initialCallInfo.getHomeNumber());
                        workNumberTF.setText(initialCallInfo.getWorkNumber());
                        addressTA.setText(initialCallInfo.getAddress());
                        birthdayDP.setValue(LocalDate.ofInstant(Instant.ofEpochMilli(initialCallInfo.getBirthDay()), ZoneId.systemDefault()));
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
        if (initialUser == null) {
            SceneManager.showNotification("Info", "Not Found!", 3);
            return;
        }

        if (personalChanged()) {
            if (firstNameTF.getText().isEmpty() || lastNameTF.getText().isEmpty() || (emailTF.getText().isEmpty()) || passwordTF.getText().isEmpty() || confirmPasswordTF.getText().isEmpty()) {
                SceneManager.showNotification("Info", "Please fill all the fields of personal section.", 3);
                return;
            }

            if (!emailTF.getText().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
                SceneManager.showNotification("Info", "Invalid email address.", 3);
                return;
            }

            if (!additionalNameTF.getText().matches("(?i)^[a-z]{1,20}$") || additionalNameTF.getText().isEmpty()) {
                SceneManager.showNotification("Info", "Additional name must be a maximum of 20 characters and consist of only letters.", 3);
                return;
            }

            if (!firstNameTF.getText().matches("(?i)^[a-z]{1,20}$")) {
                SceneManager.showNotification("Info", "First name must be a maximum of 20 characters and consist of only letters.", 3);
                return;
            }

            if (!lastNameTF.getText().matches("(?i)^[a-z]{1,40}$")) {
                SceneManager.showNotification("Info", "Last name must be a maximum of 40 characters and consist of only letters.", 3);
                return;
            }

            if (!(passwordTF.getText().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$"))) {
                SceneManager.showNotification("Info", "The password must contain 8-20 characters, at least one letter and one digit.", 3);
                return;
            }

            if (!passwordTF.getText().equals(confirmPasswordTF.getText())) {
                SceneManager.showNotification("Info", "Passwords don't match.", 3);
                return;
            }

        } else if (!confirmPasswordTF.getText().equals(passwordTF.getText())) {
            SceneManager.showNotification("Info", "Passwords don't match.", 3);
            return;
        }

        initialUser.setFirstName(firstNameTF.getText());
        initialUser.setLastName(lastNameTF.getText());
        initialUser.setAdditionalName(additionalNameTF.getText());
        initialUser.setEmail(emailTF.getText());
        initialUser.setPassword(passwordTF.getText());

        JSONObject header = JsonHandler.createJson("Authorization", DataAccess.getJWT());
        new Thread(() -> {
            try {
                RequestBuilder.sendJsonRequest("PUT", "users", header, initialUser.toJson());
                Platform.runLater(() -> {
                    SceneManager.showNotification("Success", "Personal info Added.", 3);
                });
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            } catch (NotAcceptableException e) {
                Platform.runLater(() -> {
                    SceneManager.showNotification("Failure", "Request failed. Try Again", 3);
                });
            }
        }).start();

        new Thread(() -> {
            try {
                if (isProfileFilled()) {
                    Profile profile = new Profile(initialUser.getUserId(), bioTA.getText(), countryCB.getValue(),
                            cityTF.getText(), Profile.Status.valueOf(statusCB.getValue()),
                            Profile.Profession.valueOf(professionCB.getValue()), 1);

                    RequestBuilder.sendJsonRequest("POST", "users/profiles", header, profile.toJson());
                    Platform.runLater(() -> {
                        SceneManager.showNotification("Success", "Profile info added.", 3);
                    });
                }
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            } catch (NotAcceptableException e) {
                Platform.runLater(() -> {
                    SceneManager.showNotification("Failure", "Request failed. Try Again", 3);
                });
            }
        }).start();

        new Thread(() -> {
            try {
                if (isCallInfoFilled()) {
                    CallInfo callInfo = new CallInfo(initialUser.getUserId(), initialUser.getEmail(),
                            mobileNumberTF.getText(), homeNumberTF.getText(), workNumberTF.getText(), addressTA.getText(),
                            Date.from(birthdayDP.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            CallInfo.PrivacyPolitics.valueOf(privacyCB.getValue()), socialMediaTF.getText());

                    RequestBuilder.sendJsonRequest("POST", "users/callInfo", header, callInfo.toJson());
                    Platform.runLater(() -> {
                        SceneManager.showNotification("Success", "Call Info Added", 3);
                    });
                }
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            } catch (NotAcceptableException e) {
                Platform.runLater(() -> {
                    SceneManager.showNotification("Failure", "Request failed. Try Again", 3);
                });
            }
        }).start();

        new Thread(() -> {
            try {
                if (isEducationFilled()) {
                    Education education = new Education(initialUser.getUserId(), instituteTF.getText(), fieldTF.getText(),
                            Date.from(startDateDP.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date.from(startDateDP.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Integer.parseInt(gradeTF.getText()), activitiesTA.getText(), aboutTA.getText());

                    RequestBuilder.sendJsonRequest("POST", "users/educations", header, education.toJson());

                    if (skillTA.getText() != null) {
                        Skill skill = new Skill(initialUser.getUserId(), education.getEducationId(), skillTA.getText());

                        RequestBuilder.sendJsonRequest("POST", "users/skills", header, skill.toJson());
                        Platform.runLater(() -> {
                            SceneManager.showNotification("Success", "Education Added", 3);
                        });
                    }

                    Platform.runLater(() -> {
                        SceneManager.showNotification("Success", "Education Added", 3);
                    });
                }
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            } catch (NotAcceptableException e) {
                Platform.runLater(() -> {
                    SceneManager.showNotification("Failure", "Request failed. Try Again", 3);
                });
            }
        }).start();

        switched.set(true);
    }

    private boolean isEducationFilled() {
        return instituteTF.getText() != null && fieldTF.getText() != null &&
                startDateDP.getValue() != null && endDateDP.getValue() != null && gradeTF.getText() != null &&
                activitiesTA.getText() != null && aboutTA.getText() != null &&
                skillTA.getText() != null;
    }

    private boolean isProfileFilled() {
        return bioTA.getText() != null && countryCB.getValue() != null &&
                cityTF.getText() != null && professionCB != null &&
                statusCB.getValue() != null;
    }

    private boolean isCallInfoFilled() {
        return mobileNumberTF.getText() != null && homeNumberTF.getText() != null &&
                workNumberTF.getText() != null && addressTA.getText() != null &&
                birthdayDP.getValue() != null && socialMediaTF.getText() != null;
    }

    private boolean personalChanged() {
        return !(firstNameTF.getText().equals(initialUser.getFirstName()) && lastNameTF.getText().equals(initialUser.getLastName()) &&
                additionalNameTF.getText().equals(initialUser.getAdditionalName()) && emailTF.getText().equals(initialUser.getEmail()) &&
                passwordTF.getText().equals(initialUser.getPassword()));
    }

    public BooleanProperty isSwitched() {
        return switched;
    }
}
