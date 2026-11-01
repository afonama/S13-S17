package org.example.s19;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class StudentPortal extends Application {

    private StudentRepository repo;
    private TableView<StudentRepository.Student> table;
    private TextField nameField;
    private Button addButton, updateButton, deleteButton, searchButton;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {
        try {
            repo = new StudentRepository();
        } catch (SQLException e) {
            // FIX 5: Changed title to be more descriptive
            showAlert("Database Connection Failed", "Failed to connect to database: " + e.getMessage());
            Platform.exit();
            return;
        }

        nameField = new TextField();
        nameField.setPromptText("Student name");
        nameField.setPrefWidth(300);

        addButton = new Button("Add");
        updateButton = new Button("Update selected");
        deleteButton = new Button("Delete selected");
        searchButton = new Button("Search");

        @SuppressWarnings("unchecked")
        HBox buttonBar = new HBox(10, addButton, updateButton, deleteButton, searchButton);
        HBox inputBar = new HBox(10, nameField, buttonBar);
        inputBar.setPadding(new Insets(10));

        table = new TableView<>();
        TableColumn<StudentRepository.Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().id()));
        idCol.setPrefWidth(80);

        TableColumn<StudentRepository.Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().name()));
        nameCol.setPrefWidth(400);

        table.getColumns().addAll(idCol, nameCol);
        table.setPrefHeight(400);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                nameField.setText(newVal.name());
            }
        });

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: blue;");

        VBox root = new VBox(10, inputBar, table, statusLabel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 550);
        stage.setTitle("Aptech Student Portal");
        stage.setScene(scene);
        stage.show();

        addButton.setOnAction(e -> addStudent());
        updateButton.setOnAction(e -> updateStudent());
        deleteButton.setOnAction(e -> deleteStudent());
        searchButton.setOnAction(e -> searchStudents());

        loadAll();
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Name cannot be empty");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        runOperation("Adding student...", () -> repo.add(name), result -> {
            statusLabel.setText("Student added successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
            nameField.clear();
            loadAll();
        });
    }

    private void updateStudent() {
        StudentRepository.Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a student to update");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        String newName = nameField.getText().trim();
        if (newName.isEmpty()) {
            statusLabel.setText("Name cannot be empty");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        runOperation("Updating student...", () -> repo.update(selected.id(), newName), result -> {
            statusLabel.setText("Student updated successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
            loadAll();
        });
    }

    private void deleteStudent() {
        StudentRepository.Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a student to delete");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        runOperation("Deleting student...", () -> repo.delete(selected.id()), result -> {
            statusLabel.setText("Student deleted successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
            nameField.clear();
            loadAll();
        });
    }

    private void searchStudents() {
        String pattern = nameField.getText().trim();
        if (pattern.isEmpty()) {
            loadAll();
            return;
        }
        runOperation("Searching...", () -> repo.search(pattern), result -> {
            table.setItems(FXCollections.observableArrayList(result));
            statusLabel.setText("Found " + result.size() + " student(s)");
            statusLabel.setStyle("-fx-text-fill: blue;");
        });
    }

    private void loadAll() {
        runOperation("Loading students...", () -> repo.findAll(), result -> {
            table.setItems(FXCollections.observableArrayList(result));
            statusLabel.setText("Loaded " + result.size() + " student(s)");
            statusLabel.setStyle("-fx-text-fill: blue;");
        });
    }

    private <T> void runOperation(String message, DatabaseOperation<T> operation, OnSuccess<T> onSuccess) {
        setButtonsDisabled(true);
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: blue;");

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws SQLException {
                return operation.execute();
            }
        };

        task.setOnSucceeded(e -> {
            setButtonsDisabled(false);
            try {
                onSuccess.handle(task.getValue());
            } catch (Exception ex) {
                statusLabel.setText("Error updating UI: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        task.setOnFailed(e -> {
            setButtonsDisabled(false);
            Throwable ex = task.getException();
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            // FIX 4: Replaced printStackTrace() with robust logging
            System.err.println("Background task failed: " + ex.getMessage());
        });

        new Thread(task).start();
    }

    private void setButtonsDisabled(boolean disabled) {
        addButton.setDisable(disabled);
        updateButton.setDisable(disabled);
        deleteButton.setDisable(disabled);
        searchButton.setDisable(disabled);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FunctionalInterface
    interface DatabaseOperation<T> {
        T execute() throws SQLException;
    }

    @FunctionalInterface
    interface OnSuccess<T> {
        void handle(T result);
    }

    public static void main(String[] args) {
        launch(args);
    }
}