package org.cs250.nan.backend.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The Settings class manages application configuration settings by loading them from a configuration
 * file and providing default values if the file does not exist. It also allows saving updated settings
 * back to the file.
 *
 * <p>The configuration file, named "settings.conf", is stored in the user's home directory at a platform-dependent
 * location. On Windows systems, it is located under "AppData/Local/NAn"; on other operating systems (e.g., Linux,
 * macOS), it is stored in ".config/NAn". The file contains the following properties:
 * <ul>
 *   <li><b>dataStorage</b> - Path to the data storage directory</li>
 *   <li><b>defaultUseOfGps</b> - Flag indicating whether GPS usage is enabled by default</li>
 *   <li><b>keepHistory</b> - Flag indicating whether history is kept</li>
 *   <li><b>activateGui</b> - Flag indicating whether the GUI is activated</li>
 *   <li><b>logFile</b> - Path to the log file</li>
 * </ul>
 * </p>
 *
 * <p>If the configuration file does not exist, default properties are applied, and the file is created with these
 * settings. Methods that modify settings are synchronized to ensure thread safety, and logging is used to report
 * errors during directory creation, file loading, or saving operations.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * Settings settings = new Settings();
 * String dataStoragePath = settings.getDataStorage();
 * settings.setDataStorage("/new/data/storage");
 * settings.saveSettings();
 * }
 * </pre>
 *
 * <p>This class is annotated with {@code @Component} so that it is automatically detected and registered as a
 * Spring Bean.</p>
 *
 * @see java.nio.file.Path
 * @see java.util.Properties
 * @see org.slf4j.Logger
 * @see org.slf4j.LoggerFactory
 * @see org.springframework.stereotype.Component
 *
 * @author Marlon
 * @version 1.0
 */

@Component
public class Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    private final Path dirPath;
    private final Path configFile;
    private final Properties properties;

    /**
     * Constructs a new Settings instance.
     * <p>
     * Initializes the properties, determines the configuration directory based on the OS,
     * creates the configuration directory if it doesn't exist, and loads the settings from the configuration file.
     * If the configuration file is not found, default settings are applied and saved.
     * </p>
     */
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

    /**
     * Saves the current configuration settings to the configuration file.
     * <p>
     * The settings are stored using {@link java.util.Properties#store(OutputStream, String)}.
     * </p>
     */
    public synchronized void saveSettings() {
        try (OutputStream output = Files.newOutputStream(configFile,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.store(output, null);
        } catch (IOException ex) {
            LOGGER.error("Error saving configuration to file: {}", configFile, ex);
        }
    }

    /**
     * Retrieves the data storage path from the configuration.
     *
     * @return the path to the data storage directory as a String.
     */
    public String getDataStorage() {
        return properties.getProperty("dataStorage");
    }

    /**
     * Sets the data storage path in the configuration.
     *
     * @param dataStorage the new data storage directory path.
     */
    public synchronized void setDataStorage(String dataStorage) {
        properties.setProperty("dataStorage", dataStorage);
    }

    /**
     * Checks if GPS usage is enabled by default.
     *
     * @return true if GPS is enabled by default; false otherwise.
     */
    public boolean isDefaultUseOfGps() {
        return Boolean.parseBoolean(properties.getProperty("defaultUseOfGps"));
    }

    /**
     * Sets whether GPS usage should be enabled by default.
     *
     * @param defaultUseOfGps true to enable GPS by default; false to disable.
     */
    public synchronized void setDefaultUseOfGps(boolean defaultUseOfGps) {
        properties.setProperty("defaultUseOfGps", Boolean.toString(defaultUseOfGps));
    }

    /**
     * Checks if history keeping is enabled.
     *
     * @return true if history keeping is enabled; false otherwise.
     */
    public boolean isKeepHistory() {
        return Boolean.parseBoolean(properties.getProperty("keepHistory"));
    }

    /**
     * Sets whether history keeping is enabled.
     *
     * @param keepHistory true to enable history keeping; false to disable.
     */
    public synchronized void setKeepHistory(boolean keepHistory) {
        properties.setProperty("keepHistory", Boolean.toString(keepHistory));
    }

    /**
     * Checks if the GUI is activated.
     *
     * @return true if the GUI is activated; false otherwise.
     */
    public boolean isActivateGui() {
        return Boolean.parseBoolean(properties.getProperty("activateGui"));
    }

    /**
     * Sets whether the GUI should be activated.
     *
     * @param activateGui true to activate the GUI; false to deactivate.
     */
    public synchronized void setActivateGui(boolean activateGui) {
        properties.setProperty("activateGui", Boolean.toString(activateGui));
    }

    /**
     * Retrieves the log file path from the configuration.
     *
     * @return the log file path as a String.
     */
    public String getLogFilePath() {
        return properties.getProperty("logFile");
    }
}