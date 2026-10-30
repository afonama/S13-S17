module org.example.s17 {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;

    opens org.example.s17 to javafx.fxml;
    exports org.example.s17;
}