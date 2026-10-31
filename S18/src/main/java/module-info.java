module org.example.s18 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.s18 to javafx.fxml;
    exports org.example.s18;
}