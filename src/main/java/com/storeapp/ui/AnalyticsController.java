package com.storeapp.ui;

import com.storeapp.dao.SaleDAO;
import com.storeapp.model.Sale;
import com.storeapp.util.SceneUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AnalyticsController implements Initializable {

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label todaySalesLabel;

    @FXML
    private Label todayRevenueLabel;

    @FXML
    private Button backButton;

    @FXML
    private BarChart<String, Number> unitsBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private PieChart categoryPieChart;

    private SaleDAO saleDAO = new SaleDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnalytics();
        loadUnitsSoldChart();
        loadCategoryPieChart();
    }

    private void loadAnalytics() {
        try {
            List<Sale> allSales = saleDAO.getAllSales();
            int totalSales = allSales.size();
            double totalRevenue = allSales.stream()
                    .mapToDouble(Sale::getTotalPrice)
                    .sum();

            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            List<Sale> todaySales = saleDAO.getSalesByDateRange(startOfDay, endOfDay);
            int todaySalesCount = todaySales.size();
            double todayRevenue = todaySales.stream()
                    .mapToDouble(Sale::getTotalPrice)
                    .sum();

            totalSalesLabel.setText(String.valueOf(totalSales));
            totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
            todaySalesLabel.setText(String.valueOf(todaySalesCount));
            todayRevenueLabel.setText(String.format("$%.2f", todayRevenue));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUnitsSoldChart() {
        javafx.application.Platform.runLater(() -> {
            try {
                List<Sale> allSales = saleDAO.getAllSales();

                Map<Integer, Integer> productSales = new HashMap<>();
                Map<Integer, String> productNames = new HashMap<>();

                for (Sale sale : allSales) {
                    productSales.merge(sale.getProductId(), sale.getQuantity(), Integer::sum);
                    if (!productNames.containsKey(sale.getProductId())) {
                        productNames.put(sale.getProductId(), sale.getProductName());
                    }
                }

                unitsBarChart.setAnimated(false);
                unitsBarChart.getData().clear();
                xAxis.getCategories().clear();

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Units Sold");

                productSales.entrySet().stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                        .limit(10)
                        .forEach(entry -> {
                            String productName = productNames.getOrDefault(entry.getKey(), "Unknown");
                            String truncatedName = productName.length() > 15 ? productName.substring(0, 12) + "..."
                                    : productName;
                            String displayName = truncatedName + " (" + entry.getValue() + ")";

                            series.getData().add(new XYChart.Data<>(displayName, entry.getValue()));
                        });

                unitsBarChart.getData().add(series);
                unitsBarChart.setLegendVisible(false);

                xAxis.setTickLabelsVisible(true);
                xAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
                xAxis.setTickLabelRotation(0);
                xAxis.setLabel("Product Name");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadCategoryPieChart() {
        try {
            List<Sale> allSales = saleDAO.getAllSales();

            if (allSales.isEmpty()) {
                return;
            }

            Map<String, Integer> categoryQuantities = new HashMap<>();

            for (Sale sale : allSales) {
                String category = sale.getCategory();
                if (category != null) {
                    categoryQuantities.merge(category, sale.getQuantity(), Integer::sum);
                }
            }

            int totalQuantity = categoryQuantities.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            categoryQuantities.forEach((category, quantity) -> {
                double percentage = (quantity * 100.0) / totalQuantity;
                pieChartData.add(new PieChart.Data(
                        category + ": " + quantity + " units (" + String.format("%.1f%%", percentage) + ")",
                        quantity));
            });

            categoryPieChart.setData(pieChartData);
            categoryPieChart.setLabelsVisible(true);
            categoryPieChart.setLegendVisible(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack(javafx.event.ActionEvent event) {
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
