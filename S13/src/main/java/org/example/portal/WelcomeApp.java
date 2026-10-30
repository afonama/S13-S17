package org.example.portal;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class WelcomeApp {

    public static class App extends Application {

        @Override
        public void start(Stage primaryStage) {
            Label titleLabel = new Label("Aptech Computer Education");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            titleLabel.setTextFill(Color.DARKBLUE);

            Label subtitleLabel = new Label("JavaFX Lab - Session 13");
            subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            subtitleLabel.setTextFill(Color.GRAY);
            Label namePrompt = new Label("Student Name:");
            namePrompt.setFont(Font.font("Arial", 12));

            TextField nameField = new TextField();
            nameField.setPromptText("Enter your name");
            nameField.setPrefWidth(250);

            HBox inputRow = new HBox(10, namePrompt, nameField);
            inputRow.setAlignment(Pos.CENTER_LEFT);
            Button greetButton = new Button("Greet");
            Button clearButton = new Button("Clear");

            Label statusLabel = new Label("Enter your name and click Greet");
            statusLabel.setFont(Font.font("Arial", 12));

            greetButton.setOnAction(e -> {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    statusLabel.setText("Error: Please enter your name");
                    statusLabel.setTextFill(Color.RED);
                } else {
                    statusLabel.setText("Welcome to Aptech, " + name + "!");
                    statusLabel.setTextFill(Color.GREEN);
                }
            });

            clearButton.setOnAction(e -> {
                nameField.clear();
                statusLabel.setText("Enter your name and click Greet");
                statusLabel.setTextFill(Color.BLACK);
            });

            HBox buttonRow = new HBox(10, greetButton, clearButton);
            buttonRow.setAlignment(Pos.CENTER);
            VBox root = new VBox(15, titleLabel, subtitleLabel, inputRow, buttonRow, statusLabel);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(20));

            Scene scene = new Scene(root, 520, 280);
            primaryStage.setTitle("Aptech JavaFX Lab - Welcome App");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args); // pass the class explicitly
    }
}