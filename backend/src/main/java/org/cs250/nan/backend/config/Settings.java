package org.cs250.nan.backend.config;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static String DIR_PATH;
    private static String CONFIG_FILE;
    private final Properties properties;

    public Settings() {
        properties = new Properties();
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            DIR_PATH = userHome + File.separator + "AppData" + File.separator + "Local" + File.separator + "NAn" + File.separator;
        } else {
            DIR_PATH = userHome + File.separator + ".config" + File.separator + "NAn" + File.separator;
        }
        CONFIG_FILE = DIR_PATH + "settings.conf";
        loadSettings();
    }

    private void loadSettings() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            // Set default values if the config file does not exist
            System.out.println("Config file not found. Using default settings.");
            properties.setProperty("dataStorage", "/default/data/storage");
            properties.setProperty("defaultUseOfGps", "true");
            properties.setProperty("keepHistory", "true");
            properties.setProperty("activateGui", "true");
            properties.setProperty("logFile", DIR_PATH + "log.txt");
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

    public String getLogFile() {
        return properties.getProperty("logFile");
    }
}