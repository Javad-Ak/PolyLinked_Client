package org.aut.polylinked_client.control;

import io.github.gleidson28.GNAvatarView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.aut.polylinked_client.PolyLinked;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.model.*;
import org.aut.polylinked_client.utils.DataAccess;
import org.aut.polylinked_client.utils.JsonHandler;
import org.aut.polylinked_client.utils.RequestBuilder;
import org.aut.polylinked_client.utils.exceptions.NotAcceptableException;
import org.aut.polylinked_client.utils.exceptions.UnauthorizedException;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileController {
    private final static String fileId = "profile";
    private final static Path defaultAvatar = Path.of("src/main/resources/org/aut/polylinked_client/images/avatar.png");
    private final static Path defaultBG = Path.of("src/main/resources/org/aut/polylinked_client/images/background.jpeg");

    private final BooleanProperty switched = new SimpleBooleanProperty(false);

    @FXML
    private GNAvatarView avatar;

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox buttonBox;

    @FXML
    private ImageView background;

    @FXML
    private Text bioLabel;

    @FXML
    private Text callInfoText;

    @FXML
    private Button connectButton;

    @FXML
    private Button editButton;

    @FXML
    private Text educationText;

    @FXML
    private Button followButton;

    @FXML
    private Hyperlink followersLink;

    @FXML
    private Hyperlink followingsLink;

    @FXML
    private Label joinedDateLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private ScrollPane root;

    @FXML
    private Text skillsText;

    @FXML
    void initialize() {
        // theme observation
        SceneManager.activateTheme(root, fileId);
        SceneManager.getThemeProperty().addListener((observable, oldValue, newValue) -> {
            SceneManager.activateTheme(root, fileId);
        });

        background.fitWidthProperty().bind(SceneManager.getWidthProperty().multiply(0.9973));
        background.fitHeightProperty().bind(SceneManager.getHeightProperty().multiply(0.25));
    }

    public void setData(String userId) {
        if (DataAccess.getUserId().equals(userId)) {
            buttonBox.getChildren().clear();
            buttonBox.getChildren().addAll(editButton);
            setUpEdit(editButton, userId);
        } else {
            buttonBox.getChildren().clear();
            buttonBox.getChildren().addAll(followButton, connectButton);
            setUpFollow(followButton, userId);
            setUpConnect(connectButton, userId);
        }

        new Thread(() -> {
            try {
                JSONObject headers = JsonHandler.createJson("Authorization", DataAccess.getJWT());

                User userX = null;
                try {
                    userX = new User(RequestBuilder.jsonFromGetRequest("users/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }

                Profile profileX = null;
                try {
                    profileX = new Profile(RequestBuilder.jsonFromGetRequest("users/profiles/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }

                CallInfo callInfoX = null;
                try {
                    callInfoX = new CallInfo(RequestBuilder.jsonFromGetRequest("users/callInfo/" + userId, headers));
                } catch (NotAcceptableException ignored) {
                }

                List<Education> educations = RequestBuilder.arrayFromGetRequest(Education.class, "users/educations/" + userId, headers);
                List<Skill> skills = RequestBuilder.arrayFromGetRequest(Skill.class, "users/skills/" + userId, headers);
                List<User> followers = RequestBuilder.arrayFromGetRequest(User.class, "users/followers/" + userId, headers);
                List<User> followings = RequestBuilder.arrayFromGetRequest(User.class, "users/followings/" + userId, headers);

                User user = userX;
                Profile profile = profileX;
                CallInfo callInfo = callInfoX;
                Platform.runLater(() -> {
                    if (user != null) {
                        nameLabel.setText(user.getFirstName() + " " + user.getAdditionalName() + " " + user.getLastName());
                        File image = DataAccess.getFile(user.getUserId(), user.getMediaURL());
                        if (image != null)
                            avatar.setImage(new Image(image.toURI().toString()));
                        else
                            avatar.setImage(new Image(defaultAvatar.toUri().toString()));

                        Date date = user.getCreateDate();
                        joinedDateLabel.setText("Joined at " + new SimpleDateFormat("yyyy-MM-dd").format(date));
                    }

                    if (user != null && profile != null) {
                        File bg = DataAccess.getFile(profile.getUserId(), profile.getMediaURL());
                        if (bg != null)
                            background.setImage(new Image(bg.toURI().toString()));
                        else
                            background.setImage(new Image(defaultBG.toUri().toString()));

                        bioLabel.setText(profile.getBio());
                        locationLabel.setText(profile.getCity() + ", " + profile.getCountry());
                    }

                    if (callInfo != null) callInfoText.setText(callInfo.toString());

                    for (int i = 1; i <= educations.size(); i++)
                        educationText.setText(i + ". " + educations.get(i - 1).toString() + "\n");

                    for (int i = 1; i <= skills.size(); i++)
                        skillsText.setText(i + ". " + skills.get(i - 1).toString() + "\n");

                    followersLink.setText(followers.size() + " Followers");
                    followingsLink.setText(followings.size() + " Followings");

                    followersLink.setOnAction((ActionEvent event) -> {

                    });

                    followingsLink.setOnAction((ActionEvent event) -> {

                    });
                });
            } catch (UnauthorizedException e) {
                Platform.runLater(() -> {
                    SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                    SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                });
            }
        }).start();
    }

    public static void setUpFollow(Button followButton, String userId) {
        if (userId.equals(DataAccess.getUserId())) {
            followButton.setDisable(true);
            followButton.setVisible(false);
            return;
        }

        JSONObject followedHeader = RequestBuilder.buildHeadRequest("follows/" + userId, JsonHandler.createJson("Authorization", DataAccess.getJWT()));
        if (followedHeader != null && followedHeader.getString("Exists") != null && followedHeader.getString("Exists").equalsIgnoreCase("true")) {
            followButton.setText("Unfollow");
        } else {
            followButton.setText("  Follow  ");
        }

        followButton.setOnAction((ActionEvent event) -> {
            String method;
            String newText = followButton.getText();
            if (newText.contains("Follow")) {
                method = "POST";
                newText = "Unfollow";
            } else {
                method = "DELETE";
                newText = "  Follow  ";
            }

            String finalNewText = newText;
            new Thread(() -> {
                try {
                    RequestBuilder.sendJsonRequest(
                            method, "follows",
                            JsonHandler.createJson("Authorization", DataAccess.getJWT()),
                            new Follow(DataAccess.getUserId(), userId).toJson());

                    Platform.runLater(() -> {
                        followButton.setText(finalNewText);
                    });
                } catch (NotAcceptableException e) {
                    Platform.runLater(() -> {
                        SceneManager.showNotification("Failure", "Request failed.", 3);
                    });
                } catch (UnauthorizedException e) {
                    Platform.runLater(() -> {
                        SceneManager.setScene(SceneManager.SceneLevel.LOGIN);
                        SceneManager.showNotification("Info", "Your Authorization has failed or expired.", 3);
                    });
                }
            }).start();
        });
    }

    private static void setUpConnect(Button connectButton, String userId) {

    }

    private void setUpEdit(Button editButton, String userId) {
        editButton.setOnAction((ActionEvent event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(PolyLinked.class.getResource("fxmls/editProfile.fxml"));
                Parent parent = loader.load();

                EditProfileController controller = loader.getController();
                controller.setData(userId);

                SceneManager.switchRoot(parent, controller.isSwitched());

            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        });
    }


    public BooleanProperty isSwitched() {
        return switched;
    }
}