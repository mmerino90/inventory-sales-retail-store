package com.storeapp.dao;

import com.storeapp.db.Database;
import com.storeapp.model.AuditLog;
import com.storeapp.util.UserSession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public void logAction(String action, String entityType, int entityId, String oldValue, String newValue) {
        String query = "INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, old_value, new_value, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            UserSession session = UserSession.getInstance();
            int userId = session.getCurrentUser() != null ? session.getCurrentUser().getId() : 0;
            String username = session.getCurrentUser() != null ? session.getCurrentUser().getUsername() : "system";
            
            stmt.setInt(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, action);
            stmt.setString(4, entityType);
            stmt.setInt(5, entityId);
            stmt.setString(6, oldValue);
            stmt.setString(7, newValue);
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging audit action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<AuditLog> getAllAuditLogs() throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs ORDER BY timestamp DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                logs.add(extractAuditLog(rs));
            }
        }
        return logs;
    }

    public List<AuditLog> getAuditLogsByEntityType(String entityType) throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs WHERE entity_type = ? ORDER BY timestamp DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, entityType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(extractAuditLog(rs));
            }
        }
        return logs;
    }

    public List<AuditLog> getAuditLogsByEntityId(String entityType, int entityId) throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        String query = "SELECT * FROM audit_logs WHERE entity_type = ? AND entity_id = ? ORDER BY timestamp DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, entityType);
            stmt.setInt(2, entityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(extractAuditLog(rs));
            }
        }
        return logs;
    }

    private AuditLog extractAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(rs.getInt("entity_id"));
        log.setOldValue(rs.getString("old_value"));
        log.setNewValue(rs.getString("new_value"));
        Timestamp timestamp = rs.getTimestamp("timestamp");
        log.setTimestamp(timestamp != null ? timestamp.toLocalDateTime() : null);
        return log;
    }
}
