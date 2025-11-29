package com.storeapp.ui;

import com.storeapp.dao.UserDAO;
import com.storeapp.model.User;
import com.storeapp.util.SceneUtil;
import com.storeapp.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginController {

    private static final String APP_TITLE = "Retail Store Management System";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();
    private boolean isPasswordVisible = false;

    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordButton.setText("🙈");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordButton.setText("👁");
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                messageLabel.setText("Login successful!");
                loadDashboard(user);
            } else {
                messageLabel.setText("Invalid credentials");
            }
        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDashboard(User user) {
        try {
            UserSession.getInstance().setCurrentUser(user);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            SceneUtil.switchScene(stage, "/fxml/admin_dashboard.fxml", APP_TITLE);

            System.out.println("Dashboard loaded successfully for user: " + user.getUsername());
        } catch (Exception e) {
            messageLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
