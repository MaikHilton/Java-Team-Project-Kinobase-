module com.kinobase {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.kinobase.app        to javafx.graphics;
    opens com.kinobase.controller to javafx.fxml;
    opens com.kinobase.entity     to javafx.base;

    exports com.kinobase.app;
}
