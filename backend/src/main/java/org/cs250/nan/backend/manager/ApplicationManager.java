package org.cs250.nan.backend.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.config.SettingsService;
import org.cs250.nan.backend.database.MongoRetriever;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final MongoRetriever mongoRetriever;

    private final AtomicLong lastScanTime = new AtomicLong(0);
    private final AtomicInteger lastResultCount = new AtomicInteger(0);

    public ApplicationManager(MonitoringManager monitoringManager,
                              SingleScanService singleScanService,
                              JsonWriterService jsonWriterService,
                              AppProperties props,
                              SettingsService settingsService,
                              ConfigurableApplicationContext context,
                              MongoRetriever mongoRetriever) {
        this.monitoringManager = monitoringManager;
        this.singleScanService = singleScanService;
        this.jsonWriterService = jsonWriterService;
        this.props = props;
        this.settingsService = settingsService;
        this.context = context;
        this.mongoRetriever = mongoRetriever;
    }

    public String shutdownApplication() {
        LOGGER.info("Shutdown requested: stopping monitoring and closing context...");
        monitoringManager.stopMonitoring();
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
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

    public String updateSettings(AppProperties newProps) {
        boolean wasRunning = monitoringManager.isMonitoringActive();
        if (wasRunning) {
            monitoringManager.stopMonitoring();
        }
        // apply props…
        props.setBaseDir(newProps.getBaseDir());
        props.setDataStorage(newProps.getDataStorage());
        props.setKeepHistory(newProps.isKeepHistory());
        props.setActivateGui(newProps.isActivateGui());
        props.setLogFile(newProps.getLogFile());
        props.getDb().setRemoteEnabled(newProps.getDb().isRemoteEnabled());
        props.getDb().setRemoteUrl(newProps.getDb().getRemoteUrl());
        var m = props.getMonitor();
        var mNew = newProps.getMonitor();
        m.setScanInterval(mNew.getScanInterval());
        m.setGpsOn(mNew.isGpsOn());
        m.setKmlOutput(mNew.isKmlOutput());
        m.setCsvOutput(mNew.isCsvOutput());
        m.setJsonFileName(mNew.getJsonFileName());
        m.setKmlFileName(mNew.getKmlFileName());
        m.setCsvFileName(mNew.getCsvFileName());

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

    public CompletableFuture<String> runSingleScan(AppProperties.Monitor overrideCfg) {
        var cfg = overrideCfg != null ? overrideCfg : props.getMonitor();
        System.out.println(cfg);
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
    ) {}

    /**
     * Search MongoDB for documents whose SSID or MAC contains the given term.
     */
    public List<JSONObject> runMongoSearch(String query) {
        if (!props.getDb().isRemoteEnabled()) {
            return List.of();
        }
        List<JSONObject> ssidMatches =
                mongoRetriever.getDocumentsByKeyValueContains("SSID", query);
        //List<JSONObject> macMatches =
          //      mongoRetriever.getDocumentsByKeyValueContains("MAC", query);
        List<JSONObject> dateLocalMatches =
                mongoRetriever.getDocumentsByKeyValueContains("dateLocal", query);

        return Stream.concat(ssidMatches.stream(), dateLocalMatches.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<JSONObject> runMongoSearch() {
        return runMongoSearch("");
    }
}
