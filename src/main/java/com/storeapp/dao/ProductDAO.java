package com.storeapp.dao;

import com.storeapp.db.Database;
import com.storeapp.model.Product;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        }
        return products;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Date expiryDate = rs.getDate("expiry_date");
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("cost_price"),
            rs.getDouble("selling_price"),
            rs.getInt("quantity"),
            rs.getString("category"),
            rs.getString("supplier"),
            expiryDate != null ? expiryDate.toLocalDate() : null
        );
    }

    public Product getProductById(int id) throws SQLException {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractProduct(rs);
            }
        }
        return null;
    }

    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, description, cost_price, selling_price, quantity, category, supplier, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getCostPrice());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getQuantity());
            stmt.setString(6, product.getCategory());
            stmt.setString(7, product.getSupplier());
            stmt.setDate(8, product.getExpiryDate() != null ? Date.valueOf(product.getExpiryDate()) : null);
            stmt.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET name = ?, description = ?, cost_price = ?, selling_price = ?, quantity = ?, category = ?, supplier = ?, expiry_date = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getCostPrice());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getQuantity());
            stmt.setString(6, product.getCategory());
            stmt.setString(7, product.getSupplier());
            stmt.setDate(8, product.getExpiryDate() != null ? Date.valueOf(product.getExpiryDate()) : null);
            stmt.setInt(9, product.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
