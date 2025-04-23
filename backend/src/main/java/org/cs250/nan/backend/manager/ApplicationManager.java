package org.cs250.nan.backend.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.config.SettingsService;
import org.cs250.nan.backend.service.JsonWriterService;
import org.cs250.nan.backend.service.MonitoringManager;
import org.cs250.nan.backend.service.SingleScanService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Exposes start/stop/update monitoring, one-offs, exports, and state snapshots.
 */
@Service
@EnableAsync
public class ApplicationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationManager.class);

    private final MonitoringManager monitoringManager;
    private final SingleScanService singleScanService;
    private final JsonWriterService jsonWriterService;
    private final AppProperties props;
    private final SettingsService settingsService;
    private final ConfigurableApplicationContext context;

    private final AtomicLong lastScanTime = new AtomicLong(0);
    private final AtomicInteger lastResultCount = new AtomicInteger(0);

    public ApplicationManager(MonitoringManager monitoringManager,
                              SingleScanService singleScanService,
                              JsonWriterService jsonWriterService,
                              AppProperties props,
                              SettingsService settingsService,
                              ConfigurableApplicationContext context) {
        this.monitoringManager = monitoringManager;
        this.singleScanService = singleScanService;
        this.jsonWriterService = jsonWriterService;
        this.props = props;
        this.settingsService = settingsService;
        this.context = context;
    }

    /**
     * Requests a clean shutdown of the Spring context (and thus the JVM).
     * Returns immediately with a 200 OK; shutdown happens asynchronously.
     */
    public String shutdownApplication() {
        LOGGER.info("Shutdown requested: stopping monitoring and closing context...");
        monitoringManager.stopMonitoring();

        new Thread(() -> {
            try {
                Thread.sleep(100); // give response a chance to be sent
            } catch (InterruptedException ignored) {
            }
            int exitCode = SpringApplication.exit(context, () -> 0);
            LOGGER.info("Spring context closed, exiting JVM with code {}", exitCode);
            System.exit(exitCode);
        }, "app-shutdown-thread").start();

        return "Shutdown initiated — application will terminate shortly.";
    }

    public String startMonitoring() {
        monitoringManager.startMonitoring();
        return "Monitoring started.";
    }

    public String stopMonitoring() {
        monitoringManager.stopMonitoring();
        return "Monitoring stopped.";
    }

    /**
     * Apply *all* settings from the given snapshot, persist, and restart monitoring if needed.
     */
    public String updateSettings(AppProperties newProps) {
        boolean wasRunning = monitoringManager.isMonitoringActive();
        if (wasRunning) {
            monitoringManager.stopMonitoring();
        }

        // copy top-level
        props.setBaseDir(newProps.getBaseDir());
        props.setDataStorage(newProps.getDataStorage());
        props.setDefaultUseGps(newProps.isDefaultUseGps());
        props.setKeepHistory(newProps.isKeepHistory());
        props.setActivateGui(newProps.isActivateGui());
        props.setLogFile(newProps.getLogFile());

        // db
        props.getDb().setRemoteEnabled(newProps.getDb().isRemoteEnabled());
        props.getDb().setUri(newProps.getDb().getUri());

        // monitor
        AppProperties.Monitor m = props.getMonitor();
        AppProperties.Monitor mNew = newProps.getMonitor();
        m.setScanInterval(mNew.getScanInterval());
        m.setGpsOn(mNew.isGpsOn());
        m.setKmlOutput(mNew.isKmlOutput());
        m.setCsvOutput(mNew.isCsvOutput());
        m.setJsonFileName(mNew.getJsonFileName());
        m.setKmlFileName(mNew.getKmlFileName());
        m.setCsvFileName(mNew.getCsvFileName());

        // persist
        String persistMsg;
        try {
            settingsService.saveExternalConfig(newProps);
            persistMsg = "Settings saved to disk.";
        } catch (IOException e) {
            LOGGER.warn("Unable to persist settings to disk; continuing in-memory only", e);
            persistMsg = "WARNING: settings could not be saved: " + e.getMessage();
        }

        if (wasRunning) {
            monitoringManager.startMonitoring();
        }

        return "All settings updated. " + persistMsg;
    }

    /**
     * Runs a single scan (async), writes JSON via injected service,
     * prints full path to console, and returns summary message.
     */
    public CompletableFuture<String> runSingleScan(AppProperties.Monitor overrideCfg) {
        var cfg = overrideCfg != null ? overrideCfg : props.getMonitor();
        System.out.println("Running single scan…");

        return singleScanService.run(cfg)
                .thenApply(results -> {
                    int count = results != null ? results.size() : 0;
                    lastScanTime.set(System.currentTimeMillis());
                    lastResultCount.set(count);

                    String path = jsonWriterService.writeJsonFile(results, cfg.getJsonFileName());
                    String msg = "Single scan returned " + count + " result(s).";

                    if (path != null) {
                        msg += " Data saved to: " + path;
                        System.out.println("📂 Scan file location: " + path);
                    } else {
                        msg += " (Failed to save JSON file.)";
                        LOGGER.warn("Could not write scan JSON file.");
                    }
                    return msg;
                });
    }

    public String exportMonitoringData(String fileName) {
        List<JSONObject> data = monitoringManager.getMonitoringData();
        if (data.isEmpty()) {
            return "No monitoring data to export.";
        }
        String path = jsonWriterService.writeJsonFile(data, fileName);
        return path != null
                ? "Data exported to " + path
                : "Failed to export monitoring data.";
    }

    public List<JSONObject> getMonitoringData() {
        return monitoringManager.getMonitoringData();
    }

    public ApplicationState getApplicationState() {
        return new ApplicationState(
                monitoringManager.isMonitoringActive(),
                Instant.ofEpochMilli(lastScanTime.get()),
                lastResultCount.get()
        );
    }

    public record ApplicationState(
            boolean monitoring,
            Instant lastScanTime,
            int lastScanResultCount
    ) {
    }
}
