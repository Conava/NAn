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
 *   <li><b>monitor.scanInterval</b> - Default scan interval (in seconds) for monitoring mode</li>
 *   <li><b>monitor.gpsOn</b> - Flag indicating whether GPS is enabled for monitoring mode</li>
 *   <li><b>monitor.kmlOutput</b> - Flag indicating whether KML output is enabled for monitoring</li>
 *   <li><b>monitor.csvOutput</b> - Flag indicating whether CSV output is enabled for monitoring</li>
 *   <li><b>monitor.kmlFileName</b> - Default file name for KML output in monitoring mode</li>
 *   <li><b>monitor.csvFileName</b> - Default file name for CSV output in monitoring mode</li>
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
 *
 * // Retrieve monitor settings:
 * int interval = settings.getMonitorScanInterval();
 * boolean monitorGps = settings.isMonitorGpsOn();
 * }
 * </pre>
 *
 * <p>This class is annotated with {@code @Component} so that it is automatically detected and registered as a
 * Spring Bean.</p>
 *
 * @author Marlon
 * @version 1.1
 * @see java.nio.file.Path
 * @see java.util.Properties
 * @see org.slf4j.Logger
 * @see org.slf4j.LoggerFactory
 * @see org.springframework.stereotype.Component
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

    /**
     * Sets default properties for all configuration settings including monitoring settings.
     */
    private void setDefaultProperties() {
        properties.setProperty("dataStorage", "/default/data/storage");
        properties.setProperty("defaultUseOfGps", "true");
        properties.setProperty("keepHistory", "true");
        properties.setProperty("activateGui", "true");
        properties.setProperty("logFile", dirPath.resolve("log.txt").toString());
        properties.setProperty("monitor.scanInterval", "5");
        properties.setProperty("monitor.gpsOn", "true");
        properties.setProperty("monitor.kmlOutput", "true");
        properties.setProperty("monitor.csvOutput", "false");
        properties.setProperty("monitor.kmlFileName", "monitorKML");
        properties.setProperty("monitor.csvFileName", "monitorCSV");
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

    // Monitoring-related getters and setters:

    /**
     * Retrieves the default scan interval for monitoring mode.
     *
     * @return the monitor scan interval in seconds.
     */
    public int getMonitorScanInterval() {
        return Integer.parseInt(properties.getProperty("monitor.scanInterval"));
    }

    /**
     * Sets the default scan interval for monitoring mode.
     *
     * @param scanInterval the scan interval in seconds.
     */
    public synchronized void setMonitorScanInterval(int scanInterval) {
        properties.setProperty("monitor.scanInterval", Integer.toString(scanInterval));
    }

    /**
     * Checks if GPS is enabled for monitoring mode.
     *
     * @return true if GPS is enabled for monitoring; false otherwise.
     */
    public boolean isMonitorGpsOn() {
        return Boolean.parseBoolean(properties.getProperty("monitor.gpsOn"));
    }

    /**
     * Sets whether GPS should be enabled for monitoring mode.
     *
     * @param gpsOn true to enable GPS in monitoring; false to disable.
     */
    public synchronized void setMonitorGpsOn(boolean gpsOn) {
        properties.setProperty("monitor.gpsOn", Boolean.toString(gpsOn));
    }

    /**
     * Checks if KML output is enabled for monitoring mode.
     *
     * @return true if KML output is enabled; false otherwise.
     */
    public boolean isMonitorKmlOutput() {
        return Boolean.parseBoolean(properties.getProperty("monitor.kmlOutput"));
    }

    /**
     * Sets whether KML output should be enabled for monitoring mode.
     *
     * @param kmlOutput true to enable KML output; false to disable.
     */
    public synchronized void setMonitorKmlOutput(boolean kmlOutput) {
        properties.setProperty("monitor.kmlOutput", Boolean.toString(kmlOutput));
    }

    /**
     * Checks if CSV output is enabled for monitoring mode.
     *
     * @return true if CSV output is enabled; false otherwise.
     */
    public boolean isMonitorCsvOutput() {
        return Boolean.parseBoolean(properties.getProperty("monitor.csvOutput"));
    }

    /**
     * Sets whether CSV output should be enabled for monitoring mode.
     *
     * @param csvOutput true to enable CSV output; false to disable.
     */
    public synchronized void setMonitorCsvOutput(boolean csvOutput) {
        properties.setProperty("monitor.csvOutput", Boolean.toString(csvOutput));
    }

    /**
     * Retrieves the default file name for KML output in monitoring mode.
     *
     * @return the KML file name as a String.
     */
    public String getMonitorKmlFileName() {
        return properties.getProperty("monitor.kmlFileName");
    }

    /**
     * Sets the default file name for KML output in monitoring mode.
     *
     * @param kmlFileName the new KML file name.
     */
    public synchronized void setMonitorKmlFileName(String kmlFileName) {
        properties.setProperty("monitor.kmlFileName", kmlFileName);
    }

    /**
     * Retrieves the default file name for CSV output in monitoring mode.
     *
     * @return the CSV file name as a String.
     */
    public String getMonitorCsvFileName() {
        return properties.getProperty("monitor.csvFileName");
    }

    /**
     * Sets the default file name for CSV output in monitoring mode.
     *
     * @param csvFileName the new CSV file name.
     */
    public synchronized void setMonitorCsvFileName(String csvFileName) {
        properties.setProperty("monitor.csvFileName", csvFileName);
    }

    /**
     * Resets all settings to their default values and saves them to the configuration file.
     * <p>
     * This method is synchronized to ensure thread safety during the reset operation.
     * </p>
     */
    public synchronized void resetToDefaults() {
        setDefaultProperties();
        saveSettings();
        LOGGER.info("Settings reset to default.");
    }
}