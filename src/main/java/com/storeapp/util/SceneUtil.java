package com.storeapp.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SceneUtil {

    private static final String STYLESHEET = "/application.css";

    private SceneUtil() {
        // utility class, no instances
    }

    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
            scene.getStylesheets().add(SceneUtil.class.getResource(STYLESHEET).toExternalForm());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
