package org.example.s18;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LearningDashboard extends Application {

    private final StudentService service = new StudentService();

    private ListView<String> students;
    private PieChart chart;
    private TextField name;
    private Label status;
    private Button add;

    @Override
    public void start(Stage stage) {
        Circle badge = new Circle(10, Color.DODGERBLUE);
        Label title = new Label("Learning Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        HBox header = new HBox(10, badge, title);
        header.setAlignment(Pos.CENTER_LEFT);
        name = new TextField();
        name.setPromptText("Student name");
        add = new Button("Add student");
        name.setOnAction(e -> add.fire());
        add.setOnAction(e -> addStudent());

        HBox form = new HBox(10, name, add);
        form.setAlignment(Pos.CENTER_LEFT);

        status = new Label();
        status.setTextFill(Color.DARKRED);
        status.setPrefHeight(20);
        students = new ListView<>();
        students.setPrefHeight(150);
        students.setItems(FXCollections.observableArrayList());

        chart = new PieChart();
        chart.setPrefHeight(200);
        chart.setTitle("Registered and available seats");

        VBox root = new VBox(15, header, form, status, students, chart);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 500, 550);
        stage.setTitle("Learning Dashboard");
        stage.setScene(scene);
        stage.show();
        refresh();
    }

    private void addStudent() {
        try {
            service.add(name.getText());

            status.setText("");
            status.setTextFill(Color.DARKGREEN);
            status.setText("Successfully added!");
            name.clear();
            refresh();

        } catch (IllegalArgumentException ex) {
            status.setTextFill(Color.DARKRED);
            status.setText(ex.getMessage());
        }
    }

    private void refresh() {

        students.getItems().setAll(service.findAll());

        chart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Registered (" + service.size() + ")", service.size()),
                new PieChart.Data("Available (" + service.availableSeats() + ")", service.availableSeats())
        ));
    }

    public static void main(String[] args) {
        launch(args);
    }
}