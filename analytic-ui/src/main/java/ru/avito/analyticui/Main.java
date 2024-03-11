package ru.avito.analyticui;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.avito.analyticui.utils.ViewWindows;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ViewWindows.openMainPage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}