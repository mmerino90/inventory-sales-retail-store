package com.storeapp.ui;

import com.storeapp.dao.UserDAO;
import com.storeapp.model.User;
import com.storeapp.util.AlertUtil;
import com.storeapp.util.SceneUtil;
import com.storeapp.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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
        if (!UserSession.getInstance().isAdmin()) {
            showAlert("Access Denied", "You do not have permission to access this page.");
            handleBack(null);
            return;
        }

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleComboBox.setItems(FXCollections.observableArrayList("admin", "employee"));


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
                userDAO.addUser(new User(0, username, password, role));
                showAlert("Success", "User added successfully!");
            } else {
                selectedUser.setUsername(username);
                if (!password.isEmpty()) {
                    selectedUser.setPassword(password);
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
        passwordField.setText("");
        roleComboBox.setValue(user.getRole());
        addUserButton.setText("Update User");
    }

    private void handleDeleteUser(User user) {
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
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            SceneUtil.switchScene(stage, "/fxml/admin_dashboard.fxml", "Retail Store Management System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        AlertUtil.info(title, message);
    }
}
