module com.example.sd3coursework {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.sd3coursework to javafx.fxml;
    exports com.example.sd3coursework;

    opens models to javafx.base;
    exports models;

    exports utils;
}