package org.cs250.nan.backend.config;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final String CONFIG_FILE = System.getProperty("user.home") + File.separator + ".nan_backend_config.properties";
    private Properties properties;

    public Settings() {
        properties = new Properties();
        loadSettings();
    }

    private void loadSettings() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            // Set default values if the config file does not exist
            properties.setProperty("dataStorage", "/default/data/storage");
            properties.setProperty("logFileStorage", "/default/log/storage");
            properties.setProperty("defaultUseOfGps", "true");
            properties.setProperty("keepHistory", "true");
            properties.setProperty("activateGui", "true");
            saveSettings();
        }
    }

    public void saveSettings() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDataStorage() {
        return properties.getProperty("dataStorage");
    }

    public void setDataStorage(String dataStorage) {
        properties.setProperty("dataStorage", dataStorage);
    }

    public String getLogFileStorage() {
        return properties.getProperty("logFileStorage");
    }

    public void setLogFileStorage(String logFileStorage) {
        properties.setProperty("logFileStorage", logFileStorage);
    }

    public boolean isDefaultUseOfGps() {
        return Boolean.parseBoolean(properties.getProperty("defaultUseOfGps"));
    }

    public void setDefaultUseOfGps(boolean defaultUseOfGps) {
        properties.setProperty("defaultUseOfGps", Boolean.toString(defaultUseOfGps));
    }

    public boolean isKeepHistory() {
        return Boolean.parseBoolean(properties.getProperty("keepHistory"));
    }

    public void setKeepHistory(boolean keepHistory) {
        properties.setProperty("keepHistory", Boolean.toString(keepHistory));
    }

    public boolean isActivateGui() {
        return Boolean.parseBoolean(properties.getProperty("activateGui"));
    }

    public void setActivateGui(boolean activateGui) {
        properties.setProperty("activateGui", Boolean.toString(activateGui));
    }
}