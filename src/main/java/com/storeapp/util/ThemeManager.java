package com.storeapp.util;

import javafx.scene.Scene;

import java.io.*;
import java.util.Properties;

public class ThemeManager {
    private static ThemeManager instance;
    private static final String THEME_LIGHT = "LIGHT";
    private static final String THEME_DARK = "DARK";
    private static final String PREFS_FILE = "app.properties";
    private static final String LIGHT_CSS = "/application.css";
    private static final String DARK_CSS = "/dark-theme.css";
    
    private String currentTheme;
    private Properties properties;

    private ThemeManager() {
        properties = new Properties();
        loadPreferences();
        currentTheme = properties.getProperty("theme", THEME_LIGHT);
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (THEME_DARK.equals(currentTheme)) {
            scene.getStylesheets().add(getClass().getResource(DARK_CSS).toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource(LIGHT_CSS).toExternalForm());
        }
    }

    public void toggleTheme(Scene scene) {
        if (THEME_LIGHT.equals(currentTheme)) {
            currentTheme = THEME_DARK;
        } else {
            currentTheme = THEME_LIGHT;
        }
        applyTheme(scene);
        savePreferences();
    }

    public boolean isDarkMode() {
        return THEME_DARK.equals(currentTheme);
    }

    public String getCurrentThemeIcon() {
        return isDarkMode() ? "☀️" : "🌙";
    }

    private void loadPreferences() {
        File prefsFile = new File(PREFS_FILE);
        if (prefsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(prefsFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading preferences: " + e.getMessage());
            }
        }
    }

    private void savePreferences() {
        properties.setProperty("theme", currentTheme);
        try (FileOutputStream fos = new FileOutputStream(PREFS_FILE)) {
            properties.store(fos, "Application Preferences");
        } catch (IOException e) {
            System.err.println("Error saving preferences: " + e.getMessage());
        }
    }
}
