package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.config.Settings;
import org.cs250.nan.backend.service.singleScanManager;
import org.cs250.nan.backend.service.MonitoringManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The ShellCommands class provides shell-accessible operations to control monitoring mode,
 * update monitoring settings, and run single scans. These commands allow runtime interaction
 * with the monitoring feature using the current settings.
 */
@ShellComponent
public class ShellCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellCommands.class);

    private final singleScanManager singleScanManager;
    private final MonitoringManager monitoringManager;
    private final Settings settings;

    /**
     * Constructs the ShellCommands with injected services.
     *
     * @param singleScanManager       the manager responsible for running scans.
     * @param monitoringManager the manager responsible for continuous monitoring scans.
     * @param settings          the configuration settings for monitoring.
     */
    @Autowired
    public ShellCommands(singleScanManager singleScanManager, MonitoringManager monitoringManager, Settings settings) {
        this.singleScanManager = singleScanManager;
        this.monitoringManager = monitoringManager;
        this.settings = settings;
    }

    /**
     * Starts the monitoring mode. Continuous scans will begin using the current monitoring settings.
     *
     * @return a confirmation message indicating monitoring mode is enabled.
     */
    @ShellMethod(value = "Start monitoring mode", key = "startMonitoring")
    public String startMonitoring() {
        monitoringManager.startMonitoring();
        LOGGER.info("Monitoring mode enabled via shell.");
        return "Monitoring mode enabled.";
    }

    /**
     * Stops the monitoring mode.
     *
     * @return a confirmation message indicating monitoring mode is disabled.
     */
    @ShellMethod(value = "Stop monitoring mode", key = "stopMonitoring")
    public String stopMonitoring() {
        monitoringManager.stopMonitoring();
        LOGGER.info("Monitoring mode disabled via shell.");
        return "Monitoring mode disabled.";
    }

    /**
     * Updates the monitoring settings based on provided parameters and restarts monitoring if active.
     *
     * @param scanInterval the new scan interval (in seconds) for monitoring.
     * @param gpsOn        flag indicating whether GPS should be enabled.
     * @param kmlOutput    flag indicating whether KML output is enabled.
     * @param csvOutput    flag indicating whether CSV output is enabled.
     * @param kmlFileName  the new file name for KML output.
     * @param csvFileName  the new file name for CSV output.
     * @return a confirmation message indicating that monitoring settings have been updated.
     */
    @ShellMethod(value = "Update monitoring settings", key = "updateMonitoring")
    public String updateMonitoring(@ShellOption(defaultValue = "5") int scanInterval,
                                   @ShellOption(defaultValue = "true") boolean gpsOn,
                                   @ShellOption(defaultValue = "true") boolean kmlOutput,
                                   @ShellOption(defaultValue = "false") boolean csvOutput,
                                   @ShellOption(defaultValue = "monitorKML") String kmlFileName,
                                   @ShellOption(defaultValue = "monitorCSV") String csvFileName) {
        settings.setMonitorScanInterval(scanInterval);
        settings.setMonitorGpsOn(gpsOn);
        settings.setMonitorKmlOutput(kmlOutput);
        settings.setMonitorCsvOutput(csvOutput);
        settings.setMonitorKmlFileName(kmlFileName);
        settings.setMonitorCsvFileName(csvFileName);
        settings.saveSettings();
        // Restart monitoring if it is already active.
        monitoringManager.stopMonitoring();
        monitoringManager.startMonitoring();
        LOGGER.info("Monitoring settings updated via shell.");
        return "Monitoring settings updated.";
    }

    @ShellMethod(value = "Reset all settings to default", key = "resetSettings")
    public String resetSettings() {
        settings.resetToDefaults();
        LOGGER.info("Settings have been reset to default via shell.");
        return "Settings have been reset to default.";
    }

    /**
     * Runs a single scan using the specified parameters.
     * This operation can execute concurrently with an active monitoring mode.
     *
     * @param gpsOn        flag indicating whether GPS should be enabled.
     * @param kmlOutput    flag indicating whether KML output is enabled.
     * @param csvOutput    flag indicating whether CSV output is enabled.
     * @param jsonFileName  the file name for KML output.
     * @param kmlFileName  the file name for KML output.
     * @param csvFileName  the file name for CSV output.
     * @return a message indicating the number of results from the single scan.
     */
    @ShellMethod(value = "Run single scan", key = "singleScan")
    public String singleScan(@ShellOption(defaultValue = ShellOption.NULL) Boolean gpsOn,
                             @ShellOption(defaultValue = ShellOption.NULL) Boolean kmlOutput,
                             @ShellOption(defaultValue = ShellOption.NULL) Boolean csvOutput,
                             @ShellOption(defaultValue = ShellOption.NULL) String jsonFileName,
                             @ShellOption(defaultValue = ShellOption.NULL) String kmlFileName,
                             @ShellOption(defaultValue = ShellOption.NULL) String csvFileName) {
        Future<List<JSONObject>> futureResults = singleScanManager.runSingleScan(gpsOn, kmlOutput, csvOutput, jsonFileName, kmlFileName, csvFileName);
        try {
            List<JSONObject> results = futureResults.get();
            LOGGER.info("Single scan completed with {} result(s).", results != null ? results.size() : 0);
            return "Single scan completed with " + (results != null ? results.size() : 0) + " result(s).";
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error during single scan: {}", e.getMessage(), e);
            return "An error occurred during single scan: " + e.getMessage();
        }
    }
}