package com.storeapp.ui;

import com.storeapp.dao.ProductDAO;
import com.storeapp.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProductListController implements Initializable {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField searchField;

    @FXML
    private Label lowStockWarning;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button backButton;

    private ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Product> filteredList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Auto-populate fields when a row is selected
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        // Real-time search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue);
        });

        // Add row factory for low stock highlighting
        productTable.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null || empty) {
                    setStyle("");
                } else if (product.getQuantity() < 10) {
                    setStyle("-fx-background-color: #FED7D7; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        try {
            productList.clear();
            productList.addAll(productDAO.getAllProducts());
            filteredList.clear();
            filteredList.addAll(productList);
            productTable.setItems(filteredList);
            checkLowStock();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterProducts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredList.clear();
            filteredList.addAll(productList);
        } else {
            String lowerCaseFilter = searchText.toLowerCase().trim();
            filteredList.clear();
            filteredList.addAll(
                productList.stream()
                    .filter(product -> 
                        product.getName().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCategory().toLowerCase().contains(lowerCaseFilter) ||
                        (product.getSupplier() != null && product.getSupplier().toLowerCase().contains(lowerCaseFilter))
                    )
                    .collect(java.util.stream.Collectors.toList())
            );
        }
        productTable.setItems(filteredList);
    }

    private void checkLowStock() {
        long lowStockCount = productList.stream()
            .filter(p -> p.getQuantity() < 10)
            .count();
        
        if (lowStockCount > 0) {
            lowStockWarning.setText("⚠️" + lowStockCount + " product(s) with low stock!");
        } else {
            lowStockWarning.setText("");
        }
    }

    @FXML
    public void handleAdd(ActionEvent event) {
        try {
            Product product = new Product();
            product.setName(nameField.getText());
            product.setDescription(descriptionField.getText());
            product.setPrice(Double.parseDouble(priceField.getText()));
            product.setQuantity(Integer.parseInt(quantityField.getText()));
            product.setCategory(categoryField.getText());

            productDAO.addProduct(product);
            loadProducts();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product from the table to update.");
            return;
        }
        
        try {
            selected.setName(nameField.getText());
            selected.setDescription(descriptionField.getText());
            selected.setPrice(Double.parseDouble(priceField.getText()));
            selected.setQuantity(Integer.parseInt(quantityField.getText()));
            selected.setCategory(categoryField.getText());

            productDAO.updateProduct(selected);
            loadProducts();
            clearFields();
            showAlert("Success", "Product updated successfully!");
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for price and quantity.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product from the table to delete.");
            return;
        }
        
        try {
            productDAO.deleteProduct(selected.getId());
            loadProducts();
            clearFields();
            showAlert("Success", "Product deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete product: " + e.getMessage());
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

    private void populateFields(Product product) {
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        categoryField.setText(product.getCategory());
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        quantityField.clear();
        categoryField.clear();
        productTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleClear(ActionEvent event) {
        clearFields();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        loadView("/fxml/admin_dashboard.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 1000, 700));
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
