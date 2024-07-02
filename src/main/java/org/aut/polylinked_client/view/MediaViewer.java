package org.aut.polylinked_client.view;

import com.jfoenix.controls.JFXSlider;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.aut.polylinked_client.SceneManager;
import org.aut.polylinked_client.utils.DataAccess;

import java.io.File;
import java.util.HashMap;

public class MediaViewer extends BorderPane {
    private static final HashMap<String, MediaViewer> mediaViewers = new HashMap<>();
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private ImageView imageView;

    public static MediaViewer getMediaViewer(File file) {
        if (mediaViewers.containsKey(file.getName()))
            return mediaViewers.get(file.getName());
        else
            return new MediaViewer(file);
    }

    private MediaViewer(File file) {
        setStyle("-fx-background-color: transparent");

        switch (DataAccess.getFileType(file)) {
            case DataAccess.FileType.IMAGE -> imageViewer(file);
            case DataAccess.FileType.VIDEO -> videoViewer(file);
            case DataAccess.FileType.AUDIO -> videoViewer(file);
            default -> setCenter(null);
        }
        mediaViewers.put(file.getName(), this);
    }

    private void imageViewer(File file) {
        imageView = new ImageView(new Image(file.toURI().toString()));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageView.fitWidthProperty().bind(SceneManager.getWidthProperty().divide(2.5));
        setCenter(imageView);
    }

    private void videoViewer(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);

        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
        mediaView.fitWidthProperty().bind(SceneManager.getWidthProperty().divide(2.5));
        setCenter(mediaView);

        HBox hBox = createControlBar(mediaPlayer);
        setBottom(hBox);
    }

    private void audioViewer(File file) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        HBox hBox = createControlBar(mediaPlayer);
        setCenter(hBox);
    }

    private HBox createControlBar(MediaPlayer mediaPlayer) {
        JFXSlider timeSlider = new JFXSlider();
        timeSlider.setValue(0);

        Button playButton = new Button("Play");
        Button resetButton = new Button("Reset");

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newTime.toSeconds());
            }
        });
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (playButton.getText().equals("Play")) {
                timeSlider.setValue(0);
            } else if (timeSlider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
            }
        });

        playButton.setOnAction(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playButton.setText("Play");
            } else {
                mediaPlayer.play();
                playButton.setText("Pause");
            }
        });

        resetButton.setOnAction(event -> {
            mediaPlayer.stop();
            playButton.setText("Play");
            timeSlider.setValue(0);
        });

        mediaPlayer.setOnReady(() -> {
            timeSlider.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            playButton.setText("Play");
            timeSlider.setValue(0);
        });

        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: blue;");
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(playButton, resetButton, timeSlider);
        return hBox;
    }
}