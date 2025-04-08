package org.cs250.nan.backend.manager;

import org.cs250.nan.backend.config.Settings;
import org.cs250.nan.backend.service.MonitoringManager;
import org.cs250.nan.backend.service.singleScanManager;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Centralized application manager containing the core logic
 * used by both the shell commands and the API controller.
 */
@Service
public class ApplicationManager {

    private final MonitoringManager monitoringManager;
    private final singleScanManager singleScanManager;
    private final Settings settings;

    public ApplicationManager(MonitoringManager monitoringManager,
                              singleScanManager singleScanManager,
                              Settings settings) {
        this.monitoringManager = monitoringManager;
        this.singleScanManager = singleScanManager;
        this.settings = settings;
    }

    /**
     * Starts monitoring mode, running continuous scans.
     *
     * @return status message
     */
    public String doStartMonitoring() {
        monitoringManager.startMonitoring();
        return "Monitoring mode enabled.";
    }

    /**
     * Stops monitoring mode.
     *
     * @return status message
     */
    public String doStopMonitoring() {
        monitoringManager.stopMonitoring();
        return "Monitoring mode disabled.";
    }

    /**
     * Updates monitoring settings and restarts monitoring if active.
     *
     * @param scanInterval interval for scans
     * @param gpsOn        whether GPS is enabled
     * @param kmlOutput    whether KML output is enabled
     * @param csvOutput    whether CSV output is enabled
     * @param kmlFileName  name for KML file
     * @param csvFileName  name for CSV file
     * @return status message
     */
    public String doUpdateMonitoring(int scanInterval,
                                     boolean gpsOn,
                                     boolean kmlOutput,
                                     boolean csvOutput,
                                     String kmlFileName,
                                     String csvFileName) {
        settings.setMonitorScanInterval(scanInterval);
        settings.setMonitorGpsOn(gpsOn);
        settings.setMonitorKmlOutput(kmlOutput);
        settings.setMonitorCsvOutput(csvOutput);
        settings.setMonitorKmlFileName(kmlFileName);
        settings.setMonitorCsvFileName(csvFileName);
        settings.saveSettings();
        monitoringManager.stopMonitoring();
        monitoringManager.startMonitoring();
        return "Monitoring settings updated.";
    }

    /**
     * Resets settings to default.
     *
     * @return status message
     */
    public String doResetSettings() {
        settings.resetToDefaults();
        return "Settings have been reset to default.";
    }

    /**
     * Runs a single scan using provided parameters.
     *
     * @return status message with the number of results or an error message
     */
    public String doSingleScan(Boolean gpsOn,
                               Boolean kmlOutput,
                               Boolean csvOutput,
                               Integer scanInterval,
                               String kmlFileName,
                               String csvFileName) {
        Future<List<JSONObject>> futureResults = singleScanManager.runSingleScan(
                gpsOn, kmlOutput, csvOutput, scanInterval, kmlFileName, csvFileName);
        try {
            List<JSONObject> results = futureResults.get();
            int count = (results != null ? results.size() : 0);
            return "Single scan completed with " + count + " result(s).";
        } catch (InterruptedException | ExecutionException e) {
            return "An error occurred during single scan: " + e.getMessage();
        }
    }
}