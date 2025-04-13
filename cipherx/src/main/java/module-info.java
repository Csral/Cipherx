module com.godgamer {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.godgamer.frontend to javafx.fxml;
    exports com.godgamer.frontend;
    exports com.godgamer.backend.Encryption;
    exports com.godgamer.backend.Cryptography;
    exports com.godgamer.backend.Handler;
}
