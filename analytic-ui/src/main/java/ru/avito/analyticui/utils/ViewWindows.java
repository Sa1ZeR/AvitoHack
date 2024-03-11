package ru.avito.analyticui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import ru.avito.analyticui.Main;

import java.io.IOException;

public class ViewWindows {

    public static void openMainPage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main.fxml"));
        final Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("Analytic UI");
        stage.setScene(scene);
        stage.show();
    }
}
