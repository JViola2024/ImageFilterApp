module hu.viola.imagefilterapp {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens hu.viola.imagefilterapp to javafx.fxml;
    exports hu.viola.imagefilterapp;
}