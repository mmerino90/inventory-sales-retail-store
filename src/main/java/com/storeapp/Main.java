package com.storeapp;

import com.storeapp.util.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneUtil.switchScene(primaryStage, "/fxml/login.fxml");
        primaryStage.setTitle("Retail Store Management System");
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
