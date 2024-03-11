module helpcreator {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;

    opens ru.avito.analyticui to javafx.fxml;
    exports ru.avito.analyticui;
    exports ru.avito.analyticui.controller;
    opens ru.avito.analyticui.controller to javafx.fxml;
}