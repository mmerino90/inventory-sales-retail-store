package com.storeapp.ui;

import com.storeapp.dao.UserDAO;
import com.storeapp.model.User;
import com.storeapp.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button addUserButton;

    @FXML
    private Button backButton;

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private User selectedUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if user is admin
        if (!UserSession.getInstance().isAdmin()) {
            showAlert("Access Denied", "You do not have permission to access this page.");
            handleBack(null);
            return;
        }

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Setup role combo box
        roleComboBox.setItems(FXCollections.observableArrayList("admin", "employee"));

        // Add action buttons (Edit and Delete)
        actionsColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        try {
            usersList.clear();
            usersList.addAll(userDAO.getAllUsers());
            usersTable.setItems(usersList);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Missing Information", "Please fill all fields.");
            return;
        }

        try {
            if (selectedUser == null) {
                // Add new user
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password); // TODO: Hash password
                newUser.setRole(role);
                userDAO.addUser(newUser);
                showAlert("Success", "User added successfully!");
            } else {
                // Update existing user
                selectedUser.setUsername(username);
                if (!password.isEmpty()) {
                    selectedUser.setPassword(password); // TODO: Hash password
                }
                selectedUser.setRole(role);
                userDAO.updateUser(selectedUser);
                showAlert("Success", "User updated successfully!");
                selectedUser = null;
                addUserButton.setText("Add User");
            }

            loadUsers();
            handleClear(null);
        } catch (SQLException e) {
            showAlert("Database Error", "Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditUser(User user) {
        selectedUser = user;
        usernameField.setText(user.getUsername());
        passwordField.setText(""); // Don't show password
        roleComboBox.setValue(user.getRole());
        addUserButton.setText("Update User");
    }

    private void handleDeleteUser(User user) {
        // Prevent deleting current user
        if (user.getId() == UserSession.getInstance().getCurrentUser().getId()) {
            showAlert("Cannot Delete", "You cannot delete your own account.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userDAO.deleteUser(user.getId());
                    loadUsers();
                    showAlert("Success", "User deleted successfully!");
                } catch (SQLException e) {
                    showAlert("Database Error", "Error deleting user: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void handleClear(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
        selectedUser = null;
        addUserButton.setText("Add User");
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin_dashboard.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            
            // Get screen bounds for full-screen experience
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
            
            Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
