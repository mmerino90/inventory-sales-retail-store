package com.storeapp.ui;

import com.storeapp.dao.ProductDAO;
import com.storeapp.dao.SaleDAO;
import com.storeapp.model.Product;
import com.storeapp.model.Sale;
import com.storeapp.util.AlertUtil;
import com.storeapp.util.ExportUtil;
import com.storeapp.util.SceneUtil;
import com.storeapp.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class SalesController implements Initializable {

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, Integer> idColumn;

    @FXML
    private TableColumn<Sale, Integer> productIdColumn;

    @FXML
    private TableColumn<Sale, String> productNameColumn;

    @FXML
    private TableColumn<Sale, Integer> quantityColumn;

    @FXML
    private TableColumn<Sale, Double> totalPriceColumn;

    @FXML
    private TableColumn<Sale, LocalDateTime> saleDateColumn;

    @FXML
    private TableColumn<Sale, Void> actionsColumn;

    @FXML
    private ComboBox<Product> productComboBox;

    @FXML
    private ComboBox<String> filterProductCombo;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TextField quantityField;

    @FXML
    private Button addSaleButton;

    @FXML
    private Button backButton;

    @FXML
    private Button exportCsvButton;

    @FXML
    private Button exportPdfButton;

    private SaleDAO saleDAO = new SaleDAO();
    private ProductDAO productDAO = new ProductDAO();
    private ObservableList<Sale> salesList = FXCollections.observableArrayList();
    private ObservableList<Sale> filteredSalesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        // Format date column to display in readable format
        saleDateColumn.setCellFactory(column -> new TableCell<Sale, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
                }
            }
        });

        // Add delete button to actions column
        actionsColumn.setCellFactory(param -> new TableCell<Sale, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Sale sale = getTableView().getItems().get(getIndex());
                    handleDeleteSale(sale);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        loadSales();
        loadProducts();
        initializeFilters();
    }

    private void initializeFilters() {
        // Populate filter product combo with all products
        try {
            ObservableList<String> productNames = FXCollections.observableArrayList();
            productNames.add("All Products");
            for (Product p : productDAO.getAllProducts()) {
                productNames.add(p.getName());
            }
            filterProductCombo.setItems(productNames);
            filterProductCombo.setValue("All Products");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSales() {
        try {
            salesList.clear();
            salesList.addAll(saleDAO.getAllSales());
            filteredSalesList.clear();
            filteredSalesList.addAll(salesList);
            salesTable.setItems(filteredSalesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFilter(ActionEvent event) {
        filteredSalesList.clear();

        java.time.LocalDate fromDate = fromDatePicker.getValue();
        java.time.LocalDate toDate = toDatePicker.getValue();
        String selectedProduct = filterProductCombo.getValue();

        filteredSalesList.addAll(
                salesList.stream()
                        .filter(sale -> {
                            // Filter by date range
                            if (fromDate != null && sale.getSaleDate().toLocalDate().isBefore(fromDate)) {
                                return false;
                            }
                            if (toDate != null && sale.getSaleDate().toLocalDate().isAfter(toDate)) {
                                return false;
                            }
                            // Filter by product
                            if (selectedProduct != null && !selectedProduct.equals("All Products") && !sale.getProductName().equals(selectedProduct)) {
                                return false;
                            }
                            return true;
                        })
                        .collect(java.util.stream.Collectors.toList())
        );

        salesTable.setItems(filteredSalesList);
    }

    @FXML
    public void handleClearFilter(ActionEvent event) {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        filterProductCombo.setValue("All Products");
        filteredSalesList.clear();
        filteredSalesList.addAll(salesList);
        salesTable.setItems(filteredSalesList);
    }

    private void loadProducts() {
        try {
            ObservableList<Product> products = FXCollections.observableArrayList(productDAO.getAllProducts());
            productComboBox.setItems(products);

            // Set cell factory to display product names
            productComboBox.setCellFactory(param -> new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " ($" + String.format("%.2f", item.getPrice()) + ")");
                    }
                }
            });

            // Set button cell to display selected product name
            productComboBox.setButtonCell(new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " ($" + String.format("%.2f", item.getPrice()) + ")");
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddSale(ActionEvent event) {
        Product selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && !quantityField.getText().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityField.getText());

                // Validate stock availability
                if (quantity <= 0) {
                    showAlert("Invalid Quantity", "Quantity must be greater than 0.");
                    return;
                }

                if (quantity > selectedProduct.getQuantity()) {
                    showAlert("Insufficient Stock",
                            "Cannot sell " + quantity + " units. Only " +
                                    selectedProduct.getQuantity() + " units available in stock.");
                    return;
                }

                double totalPrice = selectedProduct.getPrice() * quantity;

                Sale sale = new Sale();
                sale.setProductId(selectedProduct.getId());
                sale.setProductName(selectedProduct.getName());
                sale.setQuantity(quantity);
                sale.setTotalPrice(totalPrice);
                sale.setSaleDate(LocalDateTime.now());
                // Get current user ID from session
                sale.setUserId(UserSession.getInstance().getCurrentUser().getId());

                saleDAO.addSale(sale);

                // Update product quantity
                selectedProduct.setQuantity(selectedProduct.getQuantity() - quantity);
                productDAO.updateProduct(selectedProduct);

                loadSales();
                loadProducts(); // Reload products to show updated stock
                quantityField.clear();
                productComboBox.getSelectionModel().clearSelection();

                showAlert("Success", "Sale added successfully!");
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for quantity.");
            } catch (SQLException e) {
                showAlert("Database Error", "Error adding sale: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("Missing Information", "Please select a product and enter quantity.");
        }
    }

    private void handleDeleteSale(Sale sale) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Sale");
        confirmAlert.setContentText("Are you sure you want to delete this sale? This will restore the product stock.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Get product and restore stock
                    Product product = productDAO.getProductById(sale.getProductId());
                    if (product != null) {
                        product.setQuantity(product.getQuantity() + sale.getQuantity());
                        productDAO.updateProduct(product);
                    }

                    // Delete the sale
                    saleDAO.deleteSale(sale.getId());

                    loadSales();
                    loadProducts();
                    showAlert("Success", "Sale deleted and stock restored.");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete sale: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        // Minimal refactor: delegate to shared AlertUtil
        AlertUtil.info(title, message);
    }

    @FXML
    public void handleExportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Sales to CSV");
        fileChooser.setInitialFileName("sales_report_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());
        if (file != null) {
            try {
                // Export currently filtered sales
                ExportUtil.exportToCSV(filteredSalesList, file);
                showAlert("Export Successful", 
                        "Sales report exported to CSV successfully!\n" +
                        "Exported " + filteredSalesList.size() + " sales records.\n\n" +
                        "File saved to: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Export Failed", "Failed to export CSV: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleExportPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Sales to PDF");
        fileChooser.setInitialFileName("sales_report_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file != null) {
            try {
                // Export currently filtered sales
                ExportUtil.exportToPDF(filteredSalesList, file);
                showAlert("Export Successful", 
                        "Sales report exported to PDF successfully!\n" +
                        "Exported " + filteredSalesList.size() + " sales records.\n\n" +
                        "File saved to: " + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Export Failed", "Failed to export PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        loadView("/fxml/admin_dashboard.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
            SceneUtil.switchScene(stage, fxmlPath, "Retail Store Management System");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
