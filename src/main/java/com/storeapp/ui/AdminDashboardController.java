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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserSession session = UserSession.getInstance();
        
        // Set welcome message
        if (session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + session.getCurrentUser().getUsername() + " (" + session.getCurrentUser().getRole() + ")");
        }
        
        // Hide/show buttons based on role
        if (session.isAdmin()) {
            // Admin sees everything
            productsButton.setVisible(true);
            salesButton.setVisible(true);
            analyticsButton.setVisible(true);
            usersButton.setVisible(true);
        } else {
            // Employees see only Sales and Analytics (as inventory check)
            productsButton.setVisible(false);
            usersButton.setVisible(false);
            salesButton.setVisible(true);
            analyticsButton.setVisible(true);
            analyticsButton.setText("View Inventory");
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
            Stage stage = (Stage) productsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
