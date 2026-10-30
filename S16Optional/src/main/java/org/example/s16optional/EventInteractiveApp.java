package org.example.s16optional;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EventInteractiveApp extends Application {

    public static final EventType<SessionCreatedEvent> SESSION_CREATED =
            new EventType<>(Event.ANY, "SESSION_CREATED");

    public static class SessionCreatedEvent extends Event {
        private final String title;
        public SessionCreatedEvent(String title) {
            super(SESSION_CREATED);
            this.title = title;
        }
        public String getTitle() { return title; }
    }
    private int sessionCount = 0;
    private Label countBadge;
    private VBox morningCol, afternoonCol;
    private TextArea eventLog;

    @Override
    public void start(Stage stage) {
        Label logo = new Label("\uD83C\uDFEB Aptech Smart Portal");
        logo.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#a5b4fc;");

        countBadge = new Label("0 sessions");
        countBadge.setStyle("-fx-background-color:#7c3aed; -fx-text-fill:white; "
                + "-fx-background-radius:12; -fx-padding:4 12; -fx-font-size:12px;");

        Button newBtn = new Button("+ New (Ctrl+N)");
        newBtn.setStyle("-fx-background-color:#4f46e5; -fx-text-fill:white;");

        HBox topBar = new HBox(16, logo, new Region(), countBadge, newBtn);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);
        topBar.setPadding(new Insets(10, 18, 10, 18));
        topBar.setStyle("-fx-background-color:#1e1b4b;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        morningCol   = column("Morning Sessions");
        afternoonCol = column("Afternoon Sessions");

        HBox columns = new HBox(16, morningCol, afternoonCol);
        columns.setPadding(new Insets(16));
        VBox.setVgrow(columns, Priority.ALWAYS);
        eventLog = new TextArea();
        eventLog.setEditable(false);
        eventLog.setPrefHeight(130);
        eventLog.setStyle("-fx-font-family:monospace; -fx-font-size:11px; "
                + "-fx-background-color:#0a0a1a; -fx-text-fill:#94a3b8;");
        Label logTitle = new Label("Event Log:");
        logTitle.setStyle("-fx-text-fill:#64748b; -fx-font-size:11px;");
        VBox logBox = new VBox(2, logTitle, eventLog);
        logBox.setPadding(new Insets(0, 16, 10, 16));
        VBox formPane = buildNewSessionForm(stage);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(columns);
        root.setBottom(logBox);
        root.setStyle("-fx-background-color:#0f172a;");

        Scene scene = new Scene(root, 820, 560);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.N) {
                log("[KEY ] Ctrl+N — open new session form");
                toggleForm(root, formPane);
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE && root.getRight() != null) {
                log("[KEY ] Escape — close form");
                root.setRight(null);
                e.consume();
            } else if (e.isControlDown() && e.getCode() == KeyCode.S) {
                log("[KEY ] Ctrl+S — save (keyboard shortcut)");
                e.consume();
            }
        });
        root.addEventHandler(SESSION_CREATED, e -> {
            sessionCount++;
            countBadge.setText(sessionCount + " session" + (sessionCount == 1 ? "" : "s"));
            log("[CUSTOM] SessionCreatedEvent: \"" + e.getTitle() + "\" — total=" + sessionCount);
        });
        newBtn.setOnAction(e -> toggleForm(root, formPane));
        addCard(morningCol, "Java Generics (10:00)", true);
        addCard(afternoonCol, "JavaFX Layouts (14:00)", false);

        stage.setTitle("Session 16 Lab — Smart Course Portal");
        stage.setScene(scene);
        stage.show();

        log("Portal ready. Ctrl+N to create a session, double-click card to activate.");
    }
    private VBox column(String header) {
        Label title = new Label(header);
        title.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#c4b5fd;");
        VBox col = new VBox(10, title);
        col.setPadding(new Insets(12));
        col.setStyle("-fx-background-color:#1e293b; -fx-background-radius:10;");
        col.setPrefWidth(360);
        col.setUserData("column");
        col.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
                col.setStyle("-fx-background-color:#1e3a5f; -fx-background-radius:10;");
            }
            e.consume();
        });
        col.setOnDragExited(e ->
                col.setStyle("-fx-background-color:#1e293b; -fx-background-radius:10;"));
        col.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString()) {
                addCard(col, db.getString(), false);
                log("[DnD ] Dropped \"" + db.getString() + "\" into " + header);
                e.setDropCompleted(true);
            }
            e.consume();
        });
        return col;
    }
    private void addCard(VBox column, String title, boolean active) {
        Circle dot = new Circle(5, active ? Color.web("#4ade80") : Color.web("#64748b"));
        Label lbl  = new Label(title);
        lbl.setStyle("-fx-text-fill:#e2e8f0; -fx-font-size:13px;");
        HBox card  = new HBox(10, dot, lbl);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setStyle("-fx-background-color:#273348; -fx-background-radius:8; -fx-cursor:hand;");
        card.setOnMouseEntered(e ->
                card.setStyle("-fx-background-color:#334155; -fx-background-radius:8; -fx-cursor:hand;"));
        card.setOnMouseExited(e ->
                card.setStyle("-fx-background-color:#273348; -fx-background-radius:8; -fx-cursor:hand;"));
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                dot.setFill(Color.web("#4ade80"));
                log("[MOUSE] Double-click — activated: \"" + title + "\"");
            }
        });
        card.setOnDragDetected(e -> {
            log("[DnD ] Drag detected: \"" + title + "\"");
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(title);
            db.setContent(cc);
            e.consume();
        });
        card.setOnDragDone(e -> {
            if (e.getTransferMode() == TransferMode.MOVE) {
                column.getChildren().remove(card);
                log("[DnD ] Drag done: removed original card from source");
            }
            e.consume();
        });
        ContextMenu ctx = new ContextMenu();
        MenuItem edit   = new MenuItem("Edit");
        MenuItem delete = new MenuItem("Delete");

        edit.setOnAction(e -> log("[CTX ] Edit: \"" + title + "\""));
        delete.setOnAction(e -> {
            column.getChildren().remove(card);
            sessionCount--; // Decrement count on delete
            countBadge.setText(sessionCount + " session" + (sessionCount == 1 ? "" : "s"));
            log("[CTX ] Deleted: \"" + title + "\" — total=" + sessionCount);
        });

        ctx.getItems().addAll(edit, delete);
        card.setOnContextMenuRequested(e -> {
            ctx.show(card, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        column.getChildren().add(card);

        // Fire custom event so the root handler can count sessions
        card.fireEvent(new SessionCreatedEvent(title));
    }
    private VBox buildNewSessionForm(Stage stage) {
        TextField titleField = new TextField();
        titleField.setPromptText("Session title");

        TextField maxField = new TextField();
        maxField.setPromptText("Max students (numbers only)");

        // ── 3. EVENT FILTER: block non-digit characters ─────────────────────
        maxField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (!e.getCharacter().matches("[0-9]")) {
                log("[FILTER] Blocked non-digit key: '" + e.getCharacter() + "'");
                e.consume();
            }
        });
        maxField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                log("[FILTER] Blocked non-digit paste/input: '" + newVal + "'");
                maxField.setText(oldVal); // Revert to last valid state
            }
        });

        ComboBox<String> slotBox = new ComboBox<>();
        slotBox.getItems().addAll("Morning", "Afternoon");
        slotBox.setValue("Morning");

        Button saveBtn   = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.setStyle("-fx-background-color:#7c3aed; -fx-text-fill:white;");

        HBox btnRow = new HBox(10, saveBtn, cancelBtn);
        VBox form = new VBox(12,
                new Label("New Session"),
                new Label("Title:"), titleField,
                new Label("Max Students:"), maxField,
                new Label("Slot:"), slotBox,
                btnRow);
        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color:#1e293b; -fx-pref-width:220px;");

        // Final Check: Verify empty values are rejected
        saveBtn.setOnAction(e -> {
            String t = titleField.getText().trim();
            String m = maxField.getText().trim();

            if (t.isEmpty() || m.isEmpty()) {
                log("[ERROR] Title and Max Students cannot be empty.");
                return;
            }

            VBox col = "Afternoon".equals(slotBox.getValue()) ? afternoonCol : morningCol;
            addCard(col, t + " (Max: " + m + ")", false);

            titleField.clear();
            maxField.clear();
            ((BorderPane) form.getParent()).setRight(null);
        });

        cancelBtn.setOnAction(e -> ((BorderPane) form.getParent()).setRight(null));

        return form;
    }

    private void toggleForm(BorderPane root, VBox form) {
        if (root.getRight() == null) {
            root.setRight(form);
        } else {
            root.setRight(null);
        }
    }

    private void log(String msg) {
        eventLog.appendText(msg + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}