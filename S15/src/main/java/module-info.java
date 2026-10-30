module org.example.s15 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.s15 to javafx.fxml;
    exports org.example.s15;
}