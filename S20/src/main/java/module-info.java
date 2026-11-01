module org.example.s20 {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports com.aptech.library;
    opens com.aptech.library to javafx.graphics, javafx.fxml;

    exports com.aptech.library.ui to javafx.graphics, javafx.fxml;
    opens com.aptech.library.ui to javafx.graphics, javafx.fxml;
}