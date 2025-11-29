package com.storeapp.ui;

import com.storeapp.dao.AuditLogDAO;
import com.storeapp.model.AuditLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AuditLogController {

    @FXML private TableView<AuditLog> auditLogTable;
    @FXML private TableColumn<AuditLog, Integer> idColumn;
    @FXML private TableColumn<AuditLog, LocalDateTime> timestampColumn;
    @FXML private TableColumn<AuditLog, String> usernameColumn;
    @FXML private TableColumn<AuditLog, String> actionColumn;
    @FXML private TableColumn<AuditLog, String> entityTypeColumn;
    @FXML private TableColumn<AuditLog, Integer> entityIdColumn;
    @FXML private TableColumn<AuditLog, String> oldValueColumn;
    @FXML private TableColumn<AuditLog, String> newValueColumn;
    @FXML private ComboBox<String> entityTypeComboBox;
    @FXML private ComboBox<String> actionComboBox;
    @FXML private Label statusLabel;

    private AuditLogDAO auditLogDAO;
    private ObservableList<AuditLog> allLogs;

    @FXML
    public void initialize() {
        auditLogDAO = new AuditLogDAO();
        allLogs = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        entityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        entityIdColumn.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        oldValueColumn.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));

        timestampColumn.setCellFactory(column -> new TableCell<AuditLog, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        entityTypeComboBox.setValue("All");
        actionComboBox.setValue("All");

        loadAuditLogs();
    }

    private void loadAuditLogs() {
        try {
            List<AuditLog> logs = auditLogDAO.getAllAuditLogs();
            allLogs.setAll(logs);
            auditLogTable.setItems(allLogs);
            statusLabel.setText("Total logs: " + logs.size());
        } catch (SQLException e) {
            showError("Error loading audit logs", e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        String entityType = entityTypeComboBox.getValue();
        String action = actionComboBox.getValue();

        List<AuditLog> filteredLogs = allLogs.stream()
            .filter(log -> {
                boolean matchEntity = entityType == null || entityType.equals("All") || log.getEntityType().equals(entityType);
                boolean matchAction = action == null || action.equals("All") || log.getAction().equals(action);
                return matchEntity && matchAction;
            })
            .collect(Collectors.toList());

        auditLogTable.setItems(FXCollections.observableArrayList(filteredLogs));
        statusLabel.setText("Filtered logs: " + filteredLogs.size() + " / " + allLogs.size());
    }

    @FXML
    private void handleClearFilter() {
        entityTypeComboBox.setValue("All");
        actionComboBox.setValue("All");
        auditLogTable.setItems(allLogs);
        statusLabel.setText("Total logs: " + allLogs.size());
    }

    @FXML
    private void handleRefresh() {
        loadAuditLogs();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
