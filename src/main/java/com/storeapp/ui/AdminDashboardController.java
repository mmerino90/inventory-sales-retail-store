package com.storeapp.ui;

import com.storeapp.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button productsButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button analyticsButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button logoutButton;

    @FXML
    private HBox adminOnlySection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserSession session = UserSession.getInstance();
        
        // Set welcome message
        if (session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + session.getCurrentUser().getUsername());
        }
        
        // Hide/show sections based on role
        if (session.isAdmin()) {
            // Admin sees everything
            adminOnlySection.setVisible(true);
            adminOnlySection.setManaged(true);
        } else {
            // Employees see Products and Sales only
            adminOnlySection.setVisible(false);
            adminOnlySection.setManaged(false);
        }
    }

    @FXML
    public void handleProducts(ActionEvent event) {
        loadView("/fxml/product_list.fxml");
    }

    @FXML
    public void handleSales(ActionEvent event) {
        loadView("/fxml/sales.fxml");
    }

    @FXML
    public void handleAnalytics(ActionEvent event) {
        loadView("/fxml/analytics.fxml");
    }

    @FXML
    public void handleUsers(ActionEvent event) {
        loadView("/fxml/users_management.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.getInstance().clearSession();
        loadView("/fxml/login.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlPath);
        }
    }
}
