package com.storeapp.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".retailstore";
    private static final String DB_FILE = DB_DIR + File.separator + "store.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection;
    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Path dbPath = Paths.get(DB_DIR);
                if (!Files.exists(dbPath)) {
                    Files.createDirectories(dbPath);
                    System.out.println("Created database directory: " + DB_DIR);
                }
            } catch (Exception e) {
                System.err.println("Error creating database directory: " + e.getMessage());
            }
            
            connection = DriverManager.getConnection(URL);
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }
        }
        return connection;
    }

    private static void initializeDatabase() {
        try {
            boolean tablesExist = connection.getMetaData().getTables(null, null, "users", null).next();
            
            if (!tablesExist) {
                System.out.println("Initializing database from schema.sql...");
                String schemaPath = "db/schema.sql";
                String sql;
                
                if (Files.exists(Paths.get(schemaPath))) {
                    sql = new String(Files.readAllBytes(Paths.get(schemaPath)));
                } else {
                    try (InputStream is = Database.class.getClassLoader().getResourceAsStream("schema.sql")) {
                        if (is == null) {
                            System.err.println("Could not find schema.sql");
                            return;
                        }
                        sql = new BufferedReader(new InputStreamReader(is))
                                .lines().collect(Collectors.joining("\n"));
                    }
                }
                
                String[] statements = sql.split(";");
                try (Statement stmt = connection.createStatement()) {
                    for (String statement : statements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                }
                System.out.println("Database initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
