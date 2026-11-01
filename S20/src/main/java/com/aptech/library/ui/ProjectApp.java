package com.aptech.library.ui;

import com.aptech.library.domain.Book;
import com.aptech.library.domain.BookRepository;
import com.aptech.library.domain.ProjectRules;
import com.aptech.library.domain.RepositoryException;
import com.aptech.library.domain.ValidationException;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectApp extends Application {

    private static BookRepository injectedRepository;

    private BookRepository repository;
    private final ObservableList<Book> books = FXCollections.observableArrayList();

    private TableView<Book> table;
    private TextField titleField;
    private TextField authorField;
    private TextField isbnField;
    private Label messageLabel;

    public static void injectRepository(BookRepository repository) {
        injectedRepository = Objects.requireNonNull(
                repository,
                "BookRepository must not be null"
        );
    }

    @Override
    public void init() {
        repository = Objects.requireNonNull(
                injectedRepository,
                "Call ProjectApp.injectRepository(...) before launching ProjectApp."
        );
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        root.setTop(buildInputArea());
        root.setCenter(buildTable());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 980, 600);

        stage.setTitle("Aptech Library Tracker");
        stage.setScene(scene);
        stage.show();

        loadBooks();
    }

    private VBox buildInputArea() {
        titleField = new TextField();
        authorField = new TextField();
        isbnField = new TextField();

        titleField.setPromptText("Effective Java");
        authorField.setPromptText("Joshua Bloch");
        isbnField.setPromptText("9780132350884");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);

        form.add(new Label("Title:"), 0, 0);
        form.add(titleField, 1, 0);

        form.add(new Label("Author:"), 0, 1);
        form.add(authorField, 1, 1);

        form.add(new Label("ISBN:"), 0, 2);
        form.add(isbnField, 1, 2);

        Button addButton = new Button("Add");
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> addBook());

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateBook());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteBook());

        Button toggleButton = new Button("Toggle Availability");
        toggleButton.setOnAction(e -> toggleAvailability());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadBooks());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearForm());

        HBox buttons = new HBox(
                8,
                addButton,
                updateButton,
                deleteButton,
                toggleButton,
                refreshButton,
                clearButton
        );

        VBox top = new VBox(12, form, buttons);
        top.setPadding(new Insets(12));

        return top;
    }

    private TableView<Book> buildTable() {
        table = new TableView<>(books);
        table.setPlaceholder(new Label("No books found. Add your first book."));
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldBook, newBook) -> {
            if (newBook != null) {
                titleField.setText(newBook.getTitle());
                authorField.setText(newBook.getAuthor());
                isbnField.setText(newBook.getIsbn());
            }
        });

        TableColumn<Book, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(String.valueOf(cell.getValue().getId()))
        );
        idColumn.setPrefWidth(50);

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getTitle())
        );
        titleColumn.setPrefWidth(260);

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getAuthor())
        );
        authorColumn.setPrefWidth(200);

        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getIsbn())
        );
        isbnColumn.setPrefWidth(160);

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().isAvailable() ? "Available" : "Issued")
        );
        statusColumn.setPrefWidth(100);

        statusColumn.setCellFactory(column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if ("Available".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: darkorange; -fx-font-weight: bold;");
                    }
                }
            }
        });

        table.getColumns().add(idColumn);
        table.getColumns().add(titleColumn);
        table.getColumns().add(authorColumn);
        table.getColumns().add(isbnColumn);
        table.getColumns().add(statusColumn);

        return table;
    }

    private HBox buildStatusBar() {
        messageLabel = new Label("Ready.");
        messageLabel.setPadding(new Insets(8));

        HBox statusBar = new HBox(messageLabel);
        statusBar.setPadding(new Insets(0, 0, 0, 8));

        return statusBar;
    }

    private void loadBooks() {
        try {
            List<Book> allBooks = repository.findAll();
            books.setAll(allBooks);
            setMessage("Loaded " + allBooks.size() + " book(s).", false);
        } catch (RepositoryException ex) {
            setMessage("Database error: " + ex.getMessage(), true);
        }
    }

    private void addBook() {
        try {
            String normalizedIsbn = ProjectRules.normalizeIsbn(isbnField.getText());

            Book book = new Book(
                    0,
                    titleField.getText(),
                    authorField.getText(),
                    normalizedIsbn,
                    true
            );

            ProjectRules.validate(book);

            Optional<Book> existing = repository.findByIsbn(book.getIsbn());

            if (existing.isPresent()) {
                setMessage("A book with ISBN " + book.getIsbn() + " already exists.", true);
                return;
            }

            Book saved = repository.create(book);

            books.add(saved);
            clearForm();
            setMessage("Book added successfully.", false);

        } catch (ValidationException | RepositoryException ex) {
            setMessage(ex.getMessage(), true);
        }
    }

    private void updateBook() {
        Book selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            setMessage("Select a book to update.", true);
            return;
        }

        try {
            String normalizedIsbn = ProjectRules.normalizeIsbn(isbnField.getText());

            Book updated = new Book(
                    selected.getId(),
                    titleField.getText(),
                    authorField.getText(),
                    normalizedIsbn,
                    selected.isAvailable()
            );

            ProjectRules.validate(updated);

            Optional<Book> existing = repository.findByIsbn(updated.getIsbn());

            if (existing.isPresent() && existing.get().getId() != updated.getId()) {
                setMessage("Another book already uses ISBN " + updated.getIsbn() + ".", true);
                return;
            }

            repository.update(updated);

            int index = books.indexOf(selected);

            if (index >= 0) {
                books.set(index, updated);
            }

            clearForm();
            setMessage("Book updated successfully.", false);

        } catch (ValidationException | RepositoryException ex) {
            setMessage(ex.getMessage(), true);
        }
    }

    private void deleteBook() {
        Book selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            setMessage("Select a book to delete.", true);
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getTitle() + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );

        confirm.setHeaderText("Confirm deletion");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                repository.delete(selected.getId());
                books.remove(selected);
                clearForm();
                setMessage("Book deleted successfully.", false);
            } catch (RepositoryException ex) {
                setMessage(ex.getMessage(), true);
            }
        }
    }

    private void toggleAvailability() {
        Book selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            setMessage("Select a book to toggle availability.", true);
            return;
        }

        Book updated = new Book(
                selected.getId(),
                selected.getTitle(),
                selected.getAuthor(),
                selected.getIsbn(),
                !selected.isAvailable()
        );

        try {
            repository.update(updated);

            int index = books.indexOf(selected);

            if (index >= 0) {
                books.set(index, updated);
            }

            setMessage(
                    updated.isAvailable()
                            ? "Book marked available."
                            : "Book marked issued.",
                    false
            );

        } catch (RepositoryException ex) {
            setMessage(ex.getMessage(), true);
        }
    }

    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void setMessage(String text, boolean error) {
        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(text);

        if (error) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: green;");
        }
    }
}