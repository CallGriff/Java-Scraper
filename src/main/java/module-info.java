module com.example.javascraper {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.datatransfer;
    requires java.desktop;
    requires org.jsoup;

    opens com.example.javascraper to javafx.fxml;
    exports com.example.javascraper;
}