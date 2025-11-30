module com.example.bildwiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;
    requires java.desktop;
    requires java.smartcardio;
    requires javafx.swing;

    opens com.example.bildwiz to javafx.fxml;
    exports com.example.bildwiz;
    exports com.example.bildwiz.controllers;
    opens com.example.bildwiz.controllers to javafx.fxml;
    exports com.example.bildwiz.controllers.filters;
    opens com.example.bildwiz.controllers.filters to javafx.fxml;
}