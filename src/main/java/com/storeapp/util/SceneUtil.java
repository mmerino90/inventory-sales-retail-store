package com.storeapp.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class to centralize JavaFX scene loading and window configuration.
 */
public final class SceneUtil {

    private static final String STYLESHEET_PATH = "/application.css";

    private SceneUtil() {
        // Utility class - no instances
    }

    /**
     * Loads an FXML view into the given stage, applies global stylesheets,
     * and sizes the window to the current screen.
     *
     * @param stage    the target Stage
     * @param fxmlPath path to the FXML resource (e.g. "/fxml/login.fxml")
     * @param title    window title
     * @throws IOException if the FXML cannot be loaded
     */
    public static void switchScene(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlPath));
        Parent root = loader.load();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().add(SceneUtil.class.getResource(STYLESHEET_PATH).toExternalForm());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();
    }
}
