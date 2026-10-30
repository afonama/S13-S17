package org.example.s17;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LessonPlayer extends Application {

    private MediaPlayer player;
    private final MediaView view = new MediaView();
    private final Label status = new Label("Choose a local audio or video file");
    private final Button play = new Button("Play");
    private final Button pause = new Button("Pause");
    private final Slider volume = new Slider(0, 1, 0.5);

    @Override
    public void start(Stage stage) {
        setControlsDisabled(true);
        status.setWrapText(true);
        play.setOnAction(event -> {
            if (player != null) player.play();
        });
        pause.setOnAction(event -> {
            if (player != null) player.pause();
        });
        volume.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (player != null) {
                player.setVolume(newValue.doubleValue());
            }
        });
        Button sample = new Button("Load training tone");
        sample.setOnAction(event -> loadSample("class-tone.wav"));

        Button videoSample = new Button("Load sample video");
        videoSample.setOnAction(event -> loadSample("big-buck-bunny-trailer.mp4"));

        Button open = new Button("Open audio or video");
        open.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose a local lesson recording");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Media", "*.wav", "*.mp3", "*.mp4", "*.m4v"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                load(file.toURI().toString());
            }
        });
        view.setFitWidth(560);
        view.setFitHeight(300);
        view.setPreserveRatio(true);
        VBox root = new VBox(12,
                new HBox(8, sample, videoSample, open),
                view,
                new HBox(8, play, pause, new Label("Volume"), volume),
                status);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 640, 450));
        stage.setTitle("Student lesson player");
        stage.show();
    }

    private void loadSample(String name) {
        var resource = getClass().getResource("/media/" + name);
        if (resource == null) {
            fail("Missing sample: " + name + ". Check src/main/resources/media.");
            return;
        }
        load(resource.toExternalForm());
    }
    private void load(String source) {
        setControlsDisabled(true);
        disposePlayer();

        try {
            Media media = new Media(source);
            MediaPlayer current = new MediaPlayer(media);
            player = current;

            view.setMediaPlayer(current);
            current.setVolume(volume.getValue());
            status.setText("Loading...");

            // Each callback ignores events from a player that has since been replaced
            media.setOnError(() -> {
                if (player == current) {
                    fail("Cannot read this media file: " + media.getError().getMessage());
                }
            });

            current.setOnError(() -> {
                if (player == current) {
                    fail("Cannot play this format on this computer: " + current.getError().getMessage());
                }
            });

            current.setOnReady(() -> {
                if (player == current) {
                    setControlsDisabled(false);
                    // Audio files have no picture, so hide the empty video area
                    boolean hasVideo = media.getWidth() > 0;
                    view.setVisible(hasVideo);
                    view.setManaged(hasVideo);
                    status.setText("Ready. Duration: " + formatDuration(current.getTotalDuration())
                            + ". Press Play.");
                }
            });

            current.setOnPlaying(() -> {
                if (player == current) {
                    status.setText("Playing");
                }
            });

            current.setOnPaused(() -> {
                if (player == current) {
                    status.setText("Paused");
                }
            });

            current.setOnEndOfMedia(() -> {
                if (player == current) {
                    current.stop();
                    status.setText("Finished. Press Play to replay.");
                }
            });
        } catch (IllegalArgumentException | MediaException error) {
            fail("Could not open the file: " + error.getMessage());
        }
    }

    private void disposePlayer() {
        view.setMediaPlayer(null);
        if (player != null) {
            player.dispose();
            player = null;
        }
    }

    private void setControlsDisabled(boolean disabled) {
        play.setDisable(disabled);
        pause.setDisable(disabled);
    }

    private void fail(String message) {
        setControlsDisabled(true);
        disposePlayer();
        status.setText(message);
    }

    private static String formatDuration(Duration duration) {
        if (duration == null || duration.isUnknown() || duration.isIndefinite()) {
            return "unknown";
        }
        int totalSeconds = (int) Math.round(duration.toSeconds());
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    public void stop() {
        disposePlayer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}