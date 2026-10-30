module org.example.s14i {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.s14i to javafx.fxml;
    exports org.example.s14i;
}