package org.example.s15;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class StudentDashboardApp extends Application {

    private Label feedbackLabel;
    private ToggleGroup studyModeGroup;
    private TextField nameField;

    @Override
    public void start(Stage stage) {
        Label logo = new Label(" Aptech Dashboard");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        TextField searchBox = new TextField();
        searchBox.setPromptText("Search courses...");
        searchBox.setStyle("-fx-padding: 8; -fx-background-radius: 20; -fx-border-radius: 20;");
        HBox.setHgrow(searchBox, Priority.ALWAYS);

        HBox header = new HBox(16, logo, searchBox);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #1e293b; -fx-alignment: center-left;");
        VBox kpiRow = createKpiRow();
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        HBox contentArea = new HBox(20, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        contentArea.setPadding(new Insets(20));
        VBox root = new VBox(0, header, kpiRow, contentArea);
        root.setStyle("-fx-background-color: #f1f5f9;");

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Aptech Student Dashboard");
        stage.setScene(scene);
        stage.show();
    }
    private VBox createKpiRow() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(0, 20, 20, 20));

        HBox row = new HBox(16);
        row.getChildren().addAll(
                createKpiCard("Active Students", "1,240", "#3b82f6"),
                createKpiCard("Courses Completed", "85", "#10b981"),
                createKpiCard("Pending Reviews", "12", "#f59e0b")
        );

        container.getChildren().add(row);
        return container;
    }

    private VBox createKpiCard(String title, String value, String colorHex) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox card = new VBox(8, titleLabel, valueLabel);
        card.setPadding(new Insets(20));
        card.setMinWidth(200);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                        "-fx-border-color: transparent; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12;"
        );

        // Hover Effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 4);" +
                        "-fx-border-color: " + colorHex + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                        "-fx-border-color: transparent; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12;"
        ));

        return card;
    }
    private VBox createLeftPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        panel.setMaxWidth(400);

        Label title = new Label("Enrollment Form");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label nameLabel = new Label("Student Name:");
        nameField = new TextField();
        nameField.setPromptText("Enter full name");
        studyModeGroup = new ToggleGroup();
        RadioButton modeFullTime = new RadioButton("Full-Time");
        RadioButton modePartTime = new RadioButton("Part-Time");
        RadioButton modeOnline = new RadioButton("Online Only");

        modeFullTime.setToggleGroup(studyModeGroup);
        modePartTime.setToggleGroup(studyModeGroup);
        modeOnline.setToggleGroup(studyModeGroup);
        modeFullTime.setSelected(true);

        VBox radioBox = new VBox(8, new Label("Study Mode:"), new HBox(10, modeFullTime, modePartTime, modeOnline));
        Button submitBtn = new Button("Submit Enrollment");
        submitBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6;");

        feedbackLabel = new Label("");
        feedbackLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        feedbackLabel.setWrapText(true);

        submitBtn.setOnAction(e -> validateAndSubmit());

        panel.getChildren().addAll(title, nameLabel, nameField, radioBox, submitBtn, feedbackLabel);
        return panel;
    }

    private void validateAndSubmit() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            feedbackLabel.setText("❌ Error: Student name cannot be empty.");
            feedbackLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        } else {
            String mode = ((RadioButton) studyModeGroup.getSelectedToggle()).getText();
            feedbackLabel.setText("✅ Success: " + name + " enrolled in " + mode + " mode.");
            feedbackLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px;");
        }
    }
    private VBox createRightPanel() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        panel.setMaxWidth(Double.MAX_VALUE); // Allow expansion

        Label chartTitle = new Label("Performance Analytics");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(
                new PieChart.Data("Java", 45),
                new PieChart.Data("Web Dev", 30),
                new PieChart.Data("Data Science", 25)
        );
        pieChart.setTitle("Course Distribution");
        pieChart.setLegendVisible(false);
        pieChart.setPrefSize(300, 250);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Enrollments");
        barChart.setLegendVisible(false);
        barChart.setPrefSize(300, 250);

        BarChart.Series<String, Number> series = new BarChart.Series<>();
        series.setName("Enrollments");
        series.getData().add(new BarChart.Data<>("Jan", 20));
        series.getData().add(new BarChart.Data<>("Feb", 35));
        series.getData().add(new BarChart.Data<>("Mar", 42));
        barChart.getData().add(series);
        HBox chartRow = new HBox(20, pieChart, barChart);
        chartRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(barChart, Priority.ALWAYS);

        panel.getChildren().addAll(chartTitle, chartRow);
        return panel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
