package org.example.s16;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScoreCounterApp extends Application {
    private final ScoreModel model = new ScoreModel();
    private final Label display = new Label("Score: 0");

    @Override
    public void start(Stage stage) {
        Button addButton = new Button("Add 1");
        Button subtractButton = new Button("Subtract 1");
        Button resetButton = new Button("Reset");

        connectButtons(addButton, subtractButton, resetButton);

        VBox root = new VBox(16, display, new HBox(8, addButton, subtractButton, resetButton));
        root.setPadding(new Insets(20));
        stage.setScene(new Scene(root, 420, 180));
        stage.setTitle("Student score counter");
        stage.show();
    }

    private void connectButtons(Button addButton, Button subtractButton, Button resetButton) {
        // Initial display update
        display.setText("Score: " + model.getScore());

        // Add Button Handler
        addButton.setOnAction(e -> {
            model.add();
            refreshDisplay();
        });

        // Subtract Button Handler
        subtractButton.setOnAction(e -> {
            model.subtract();
            refreshDisplay();
        });

        // Reset Button Handler
        resetButton.setOnAction(e -> {
            model.reset();
            refreshDisplay();
        });
    }

    private void refreshDisplay() {
        display.setText("Score: " + model.getScore());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
