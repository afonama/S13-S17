module org.example.s19 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // <-- THIS FIXES THE java.sql ERRORS

    opens org.example.s19 to javafx.fxml;
    exports org.example.s19;
}