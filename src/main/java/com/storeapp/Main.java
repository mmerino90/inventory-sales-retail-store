package com.storeapp;

import com.storeapp.util.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String APP_TITLE = "Retail Store Management System";

    @Override
    public void start(Stage primaryStage) {
        // Use shared helper to load the initial login view
        SceneUtil.switchScene(primaryStage, "/fxml/login.fxml", APP_TITLE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
