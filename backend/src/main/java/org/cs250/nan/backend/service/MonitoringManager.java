package org.cs250.nan.backend.service;

import org.cs250.nan.backend.config.Settings;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * It uses the settings from the Settings bean to schedule scans at fixed intervals.
 * Scan results from each execution are stored in-memory for later retrieval.
 * This class utilizes a ScheduledExecutorService to schedule the scans asynchronously.
 */
@Service
public class MonitoringManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringManager.class);

    private final ScanService scanService;
    private final Settings settings;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private final List<JSONObject> monitoringData = new CopyOnWriteArrayList<>();

    /**
     * Constructs a MonitoringManager with the provided MonitorService and Settings.
     *
     * @param scanService the service used to perform scans asynchronously.
     * @param settings       the application settings which include monitoring configuration.
     */
    @Autowired
    public MonitoringManager(ScanService scanService, Settings settings) {
        this.scanService = scanService;
        this.settings = settings;
    }

    /**
     * Starts the monitoring mode. A new scheduler is created if one is not already running.
     * The scan interval and other parameters are fetched from the Settings bean.
     */
    public synchronized void startMonitoring() {
        if (scheduler != null && !scheduler.isShutdown()) {
            LOGGER.info("Monitoring is already running.");
            return;
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        int interval = settings.getMonitorScanInterval();
        LOGGER.info("Starting monitoring with scan interval: {} seconds", interval);
        scheduledTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                // Initiate a single scan using the current monitoring settings.
                Future<List<JSONObject>> futureResults = scanService.runScanAsync(
                        settings.isMonitorGpsOn(),
                        settings.isMonitorKmlOutput(),
                        settings.isMonitorCsvOutput(),
                        interval,
                        settings.getMonitorJSONFileName(),
                        settings.getMonitorKmlFileName(),
                        settings.getMonitorCsvFileName());
                List<JSONObject> results = futureResults.get();
                if (results != null) {
                    monitoringData.addAll(results);
                    LOGGER.info("Added {} results to monitoring data.", results.size());
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error during monitoring scan: {}", e.getMessage(), e);
            }
        }, 0, interval, TimeUnit.SECONDS);
    }

    /**
     * Stops the monitoring mode by cancelling the scheduled task and shutting down the scheduler.
     */
    public synchronized void stopMonitoring() {
        if (scheduler != null) {
            scheduledTask.cancel(true);
            scheduler.shutdownNow();
            LOGGER.info("Monitoring has been stopped.");
        }
    }

    /**
     * Retrieves a copy of the monitoring data accumulated during continuous scans.
     *
     * @return list of scan result JSONObjects.
     */
    public List<JSONObject> getMonitoringData() {
        return new ArrayList<>(monitoringData);
    }
}