package org.cs250.nan.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private final Path dirPath;
    private final Path configFile;
    private final Properties properties;

    public Settings() {
        properties = new Properties();
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            dirPath = Paths.get(userHome, "AppData", "Local", "NAn");
        } else {
            dirPath = Paths.get(userHome, ".config", "NAn");
        }
        configFile = dirPath.resolve("settings.conf");
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            LOGGER.error("Failed to create configuration directory: {}", dirPath, e);
        }
        loadSettings();
    }

    private void loadSettings() {
        if (Files.exists(configFile)) {
            try (InputStream input = Files.newInputStream(configFile)) {
                properties.load(input);
            } catch (IOException ex) {
                LOGGER.error("Error loading configuration from file: {}", configFile, ex);
            }
        } else {
            LOGGER.info("Config file not found. Using default settings.");
            setDefaultProperties();
            saveSettings();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("dataStorage", "/default/data/storage");
        properties.setProperty("defaultUseOfGps", "true");
        properties.setProperty("keepHistory", "true");
        properties.setProperty("activateGui", "true");
        properties.setProperty("logFile", dirPath.resolve("log.txt").toString());
    }

    public synchronized void saveSettings() {
        try (OutputStream output = Files.newOutputStream(configFile,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.store(output, null);
        } catch (IOException ex) {
            LOGGER.error("Error saving configuration to file: {}", configFile, ex);
        }
    }

    public String getDataStorage() {
        return properties.getProperty("dataStorage");
    }

    public synchronized void setDataStorage(String dataStorage) {
        properties.setProperty("dataStorage", dataStorage);
    }

    public boolean isDefaultUseOfGps() {
        return Boolean.parseBoolean(properties.getProperty("defaultUseOfGps"));
    }

    public synchronized void setDefaultUseOfGps(boolean defaultUseOfGps) {
        properties.setProperty("defaultUseOfGps", Boolean.toString(defaultUseOfGps));
    }

    public boolean isKeepHistory() {
        return Boolean.parseBoolean(properties.getProperty("keepHistory"));
    }

    public synchronized void setKeepHistory(boolean keepHistory) {
        properties.setProperty("keepHistory", Boolean.toString(keepHistory));
    }

    public boolean isActivateGui() {
        return Boolean.parseBoolean(properties.getProperty("activateGui"));
    }

    public synchronized void setActivateGui(boolean activateGui) {
        properties.setProperty("activateGui", Boolean.toString(activateGui));
    }

    public String getLogFilePath() {
        return properties.getProperty("logFile");
    }
}