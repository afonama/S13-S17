module org.example.s16optional {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.s16optional to javafx.fxml;
    exports org.example.s16optional;
}