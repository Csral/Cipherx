module com.godgamer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.godgamer to javafx.fxml;
    exports com.godgamer;
}
