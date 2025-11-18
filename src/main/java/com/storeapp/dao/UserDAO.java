package com.storeapp.dao;

import com.storeapp.db.Database;
import com.storeapp.model.User;
import com.storeapp.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedUsername = rs.getString("username");
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");
                
                // Check if password matches (handle both hashed and plain text for migration)
                if (PasswordUtil.verifyPassword(password, storedPassword) || password.equals(storedPassword)) {
                    // If plain text matched, update to hashed version
                    if (password.equals(storedPassword)) {
                        updatePasswordHash(userId, password);
                    }
                    return new User(userId, storedUsername, storedPassword, role);
                }
            }
        }
        return null;
    }

    private void updatePasswordHash(int userId, String plainPassword) {
        try {
            String hashedPassword = PasswordUtil.hashPassword(plainPassword);
            String query = "UPDATE users SET password = ? WHERE id = ?";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, hashedPassword);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        }
        return users;
    }

    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();
        }
    }

    public void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
