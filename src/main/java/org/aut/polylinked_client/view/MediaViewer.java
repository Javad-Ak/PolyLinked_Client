package org.aut.polylinked_client.view;

import com.jfoenix.controls.JFXSlider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private MediaPlayer mediaPlayer;
    private ImageView imageView;

    public static MediaViewer getMediaViewer(File file, double relativeWidth) {
        if (relativeWidth <= 0 || relativeWidth >= 1) relativeWidth = 0.4;

        if (mediaViewers.containsKey(file.getName()))
            return mediaViewers.get(file.getName());
        else
            return new MediaViewer(file, relativeWidth);
    }

    private MediaViewer(File file, double relativeWidth) {
        switch (DataAccess.getFileType(file)) {
            case DataAccess.FileType.IMAGE -> imageViewer(file, relativeWidth);
            case DataAccess.FileType.VIDEO -> videoViewer(file, relativeWidth);
            case DataAccess.FileType.AUDIO -> audioViewer(file, relativeWidth);
            default -> setCenter(null);
        }

        mediaViewers.put(file.getName(), this);
    }

    private void imageViewer(File file, double relativeWidth) {
        imageView = new ImageView(new Image(file.toURI().toString()));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageView.fitWidthProperty().bind(SceneManager.getWidthProperty().multiply(relativeWidth));
        setCenter(imageView);
    }

    private void videoViewer(File file, double relativeWidth) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
        mediaView.fitWidthProperty().bind(SceneManager.getWidthProperty().multiply(relativeWidth));

        HBox hBox = createControlBar(mediaPlayer, relativeWidth);

        setCenter(mediaView);
        setBottom(hBox);
    }

    private void audioViewer(File file, double relativeWidth) {
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        HBox hBox = createControlBar(mediaPlayer, relativeWidth);
        setCenter(hBox);
    }

    private HBox createControlBar(MediaPlayer mediaPlayer, double relativeWidth) {
        JFXSlider timeSlider = new JFXSlider();
        timeSlider.setValue(0);
        Label label = new Label("time");

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
            int fullTime = (int) mediaPlayer.getMedia().getDuration().toSeconds();
            int seconds = fullTime % 60;
            int minutes = fullTime / 60;

            timeSlider.setMax(seconds);
            label.setText(minutes + ":" + seconds);
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            playButton.setText("Play");
            timeSlider.setValue(0);
        });

        HBox hBox = new HBox();
        hBox.setSpacing(6);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(playButton, resetButton, timeSlider, label);
        return hBox;
    }
}