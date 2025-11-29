package com.storeapp.dao;

import com.storeapp.db.Database;
import com.storeapp.model.Sale;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<Sale> getAllSales() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT s.*, p.name as product_name, p.category FROM sales s " +
                      "LEFT JOIN products p ON s.product_id = p.id " +
                      "ORDER BY s.sale_date DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("sale_date").toLocalDateTime(),
                    rs.getInt("user_id")
                );
                sale.setProductName(rs.getString("product_name"));
                sale.setCategory(rs.getString("category"));
                sales.add(sale);
            }
        }
        return sales;
    }

    public void addSale(Sale sale) throws SQLException {
        String query = "INSERT INTO sales (product_id, quantity, total_price, sale_date, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sale.getProductId());
            stmt.setInt(2, sale.getQuantity());
            stmt.setDouble(3, sale.getTotalPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(sale.getSaleDate()));
            stmt.setInt(5, sale.getUserId());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int saleId = rs.getInt(1);
                auditLogDAO.logAction("CREATE", "Sale", saleId, null, 
                    String.format("Product ID: %d - Qty: %d - Total: $%.2f", sale.getProductId(), sale.getQuantity(), sale.getTotalPrice()));
            }
        }
    }

    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT s.*, p.name as product_name, p.category FROM sales s " +
                      "LEFT JOIN products p ON s.product_id = p.id " +
                      "WHERE s.sale_date BETWEEN ? AND ? " +
                      "ORDER BY s.sale_date DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("sale_date").toLocalDateTime(),
                    rs.getInt("user_id")
                );
                sale.setProductName(rs.getString("product_name"));
                sale.setCategory(rs.getString("category"));
                sales.add(sale);
            }
        }
        return sales;
    }

    public Sale getSaleById(int id) throws SQLException {
        String query = "SELECT s.*, p.name as product_name, p.category FROM sales s " +
                      "LEFT JOIN products p ON s.product_id = p.id " +
                      "WHERE s.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("sale_date").toLocalDateTime(),
                    rs.getInt("user_id")
                );
                sale.setProductName(rs.getString("product_name"));
                sale.setCategory(rs.getString("category"));
                return sale;
            }
        }
        return null;
    }

    public void deleteSale(int id) throws SQLException {
        Sale sale = getSaleById(id);
        String query = "DELETE FROM sales WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            if (sale != null) {
                String oldValue = String.format("Product ID: %d - Qty: %d - Total: $%.2f", sale.getProductId(), sale.getQuantity(), sale.getTotalPrice());
                auditLogDAO.logAction("DELETE", "Sale", id, oldValue, null);
            }
        }
    }
}
