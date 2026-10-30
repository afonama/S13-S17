module org.example.s16 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.s16 to javafx.fxml;
    exports org.example.s16;
}